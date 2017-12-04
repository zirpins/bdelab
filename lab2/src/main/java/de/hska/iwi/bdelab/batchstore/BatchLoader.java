package de.hska.iwi.bdelab.batchstore;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailFormatFactory;
import de.hska.iwi.bdelab.schema2.*;
import manning.tap2.DataPailStructure;
import org.apache.hadoop.fs.FileSystem;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.LogManager;
import org.apache.log4j.Level;
import org.slf4j.Logger;

public class BatchLoader {
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BatchLoader.class);

    private Random rand = new Random();
    private Pail.TypedRecordOutputStream os;
    private boolean show = false;

    private static URI FILE_URI;

    static {
        try {
            FILE_URI = BatchLoader.class.getClassLoader().getResource("pageviews.txt").toURI();
        } catch (URISyntaxException e) {
            // this is not supposed to happen
            e.printStackTrace();
        }
    }

    public class Cli {
        private String[] args = null;
        private Options options = new Options();

        private final long GENERATE_LOWER_BOUND = 1000;
        private final long GENERATE_UPPER_BOUND = 1000000000;

        Cli(String[] args) {
            this.args = args;

            options.addOption("v", "verbose", false, "be verbose");
            options.addOption("h", "help", false, "show help");
            options.addOption("r", "reset", false, "reset all fact data files before importing");
            options.addOption("s", "show", false, "dump generated records to stdout (slow)");
            options.addOption("m", "master", false, "move new data to 'master' pail (otherwise they remain in 'new' pail)");
            options.addOption("g", "generate", true, "generate number of records (rounded down to full 1000th, 1000 is default)");
            options.addOption("f", "file", true, "use a non-default input file of base-records");
        }

        void parse() {
            CommandLineParser parser = new BasicParser();

            CommandLine cmd = null;
            try {
                cmd = parser.parse(options, args);

                boolean reset = false;
                boolean master = false;
                long records = 1000;

                // show records
                if (cmd.hasOption("v")) {
                    log.info("Using cli argument -v");
                    LogManager.getRootLogger().setLevel(Level.INFO);
                }

                if (cmd.hasOption("h"))
                    help();

                // reset all data
                if (cmd.hasOption("r")) {
                    log.info("Using cli argument -r");
                    reset = true;
                }

                // show records
                if (cmd.hasOption("s")) {
                    log.info("Using cli argument -s");
                    show = true;
                }

                // append to master
                if (cmd.hasOption("m")) {
                    log.info("Using cli argument -m");
                    master = true;
                }

                // generate records
                if (cmd.hasOption("g")) {
                    if (cmd.getOptionValue("g") != null) {
                        log.info("Using cli argument -g=" + cmd.getOptionValue("g"));
                        try {
                            records = Long.parseLong(cmd.getOptionValue("g"));
                            if (records < GENERATE_LOWER_BOUND || records > GENERATE_UPPER_BOUND)
                                throw new IllegalArgumentException("Number of generated records not between 1000 and " + GENERATE_UPPER_BOUND);
                        } catch (NumberFormatException nfe) {
                            log.error("Malformed option value for cli argument -g: " + nfe.getMessage());
                            help();
                        } catch (IllegalArgumentException iae) {
                            log.error("Wrong option value for cli argument -g: " + iae.getMessage());
                            help();
                        }
                    } else {
                        log.error("Missing option value for cli argument -g");
                        help();
                    }
                }

                if (cmd.hasOption("f")) {
                    if (cmd.getOptionValue("f") != null) {
                        log.info("Using cli argument -f=" + cmd.getOptionValue("f"));
                        try {
                            String filename = cmd.getOptionValue("f");
                            File file = new File(filename);
                            FILE_URI = file.toURI();
                            if (!file.exists()) throw new IllegalArgumentException("File does not exist");
                            log.info("Using input file: " + FILE_URI.toString());
                        } catch (RuntimeException re) {
                            log.error("Illegal option value for cli argument -g: " + re.getMessage());
                            System.exit(1);
                        }
                    } else {
                        log.error("Missing option value for cli argument -f");
                        help();
                        System.exit(1);
                    }
                }

                importPageviews(reset, master, records);

            } catch (ParseException e) {
                log.error("Failed to parse command line properties", e);
                help();
            }
        }

        private void help() {
            HelpFormatter formater = new HelpFormatter();
            formater.printHelp("batchloader", options, true);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        new BatchLoader().new Cli(args).parse();
    }

    private void importPageviews(boolean reset, boolean master, long records) {
        try {
            // set up filesystem
            FileSystem fs = FileUtils.getFs(false);

            // optionally reset all data files (or first time bootstrap)
            if (reset
                    || !fs.exists(new Path(FileUtils.prepareNewFactsPath(false, false)))
                    || !fs.exists(new Path(FileUtils.prepareMasterFactsPath(false, false)))) {
                resetStoreFiles();
            }

            // open existing pail directory for new facts
            Pail newPail = new Pail(fs, FileUtils.prepareNewFactsPath(false, false));

            // write facts to new pail
            os = newPail.openWrite();
            writePageviews(createPageviewsCache(), records);
            os.close();

            // optionally move newData to master while preserving the newPail to
            // receive incoming data
            if (master) {
                // open existing pail directory for master facts
                Pail masterPail = new Pail(fs, FileUtils.prepareMasterFactsPath(false, false));
                log.info("Absorbing new pail data into master pail");
                // move data away from new pail to master pail
                masterPail.absorb(newPail);
            }

            log.info("Batchloader finished.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetStoreFiles() throws IOException {
        FileSystem fs = FileUtils.getFs(false);

        String masterPathName = FileUtils.prepareMasterFactsPath(true, false);
        String newPathName = FileUtils.prepareNewFactsPath(true, false);

        Pail.create(fs, newPathName,
                PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));
        Pail.create(fs, masterPathName,
                PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()), false);
    }

    private List<String> createPageviewsCache() {
        URI uri = null;

        // Read pageviews file into cache
        List<String> cache = new LinkedList<>();
        try (Stream<String> stream = Files.lines(Paths.get(FILE_URI))) {
            stream.forEach(cache::add);
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return cache;
    }

    private void writePageviews(List<String> cache, long records) {
        long rounds = records / 1000;
        boolean gen = rounds > 1;

        if (gen)
            log.info("Generating " + rounds * 1000 + " records");

        // Create facts and write to pail
        for (long i = rounds; i > 0; i--)
            cache.forEach(line -> {
                try {
                    os.writeObject(getDatafromString(line, gen));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    private Data getDatafromString(String pageview, boolean randomize) {
        StringTokenizer tokenizer = new StringTokenizer(pageview);
        String ip = tokenizer.nextToken();
        String url = tokenizer.nextToken();
        String time = tokenizer.nextToken();
        int epoch = Integer.parseInt(time);

        if (randomize) {
            ip = rand.nextInt(256)
                    + "." + rand.nextInt(256)
                    + "." + rand.nextInt(256)
                    + "." + rand.nextInt(256);
            int DAY_IN_SECS = 60 * 60 * 24;
            epoch = epoch + rand.nextInt(DAY_IN_SECS * 2) - DAY_IN_SECS;
        }

        if (show)
            System.out.println(ip + " " + url + " " + time);

        UserID uid1 = new UserID();
        uid1.set_ip(ip);

        Page pg1 = new Page();
        pg1.set_url(url);

        PageviewEdge pve1 = new PageviewEdge();
        pve1.set_user(uid1);
        pve1.set_page(pg1);
        pve1.set_nonce(rand.nextInt());

        DataUnit du1 = new DataUnit();
        du1.set_pageview(pve1);

        Pedigree pd1 = new Pedigree();
        pd1.set_true_as_of_secs(epoch);
        Data data = new Data();
        data.set_dataunit(du1);
        data.set_pedigree(pd1);

        return data;
    }

}
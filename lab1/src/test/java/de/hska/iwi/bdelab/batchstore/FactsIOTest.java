package de.hska.iwi.bdelab.batchstore;

import static com.backtype.support.TestUtils.assertPailContents;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.hadoop.fs.FileSystem;
import org.junit.Before;
import org.junit.Test;

import com.backtype.hadoop.pail.Pail;
import com.backtype.hadoop.pail.PailFormatFactory;
import com.backtype.hadoop.pail.PailSpec;

import de.hska.iwi.bdelab.schema.Data;
import manning.tap.DataPailStructure;
import manning.tap.SplitDataPailStructure;

public class FactsIOTest {
    private final boolean LOCAL = true;

    private FileSystem fs;

    @Before
    public void setUp() throws Exception {
        fs = FileUtils.getFs(LOCAL);
    }

    @Test
    public void testStructureConstructor() throws Exception {
        String path = FileUtils.getTmpPath(fs, "friends_pail1", true, LOCAL);
        Pail p = Pail.create(fs, path, new DataPailStructure());
        PailSpec spec = p.getSpec();
        assertNotNull(spec.getName());
        assertEquals(DataPailStructure.class, spec.getStructure().getClass());
    }

    @Test
    public void testStructured() throws Exception {
        String path = FileUtils.getTmpPath(fs, "friends_pail2", true, LOCAL);

        Pail<Data> pail = Pail.create(fs, path,
                PailFormatFactory.getDefaultCopy().setStructure(new DataPailStructure()));

        Pail<Data>.TypedRecordOutputStream os = pail.openWrite();
        os.writeObject(FriendFacts.d1);
        os.writeObject(FriendFacts.d2);
        os.writeObject(FriendFacts.d3);
        os.close();

        pail = new Pail(fs, path);
        assertPailContents(pail, FriendFacts.d1, FriendFacts.d2, FriendFacts.d3);
    }

    @Test
    public void testStructuredWithPartotions() throws Exception {
        String path = FileUtils.getTmpPath(fs, "friends_pail3", true, LOCAL);

        Pail<Data> pail = Pail.create(fs, path,
                PailFormatFactory.getDefaultCopy().setStructure(new SplitDataPailStructure()));

        Pail<Data>.TypedRecordOutputStream os = pail.openWrite();
        os.writeObject(FriendFacts.d1);
        os.writeObject(FriendFacts.d2);
        os.writeObject(FriendFacts.d3);
        os.close();

        pail = new Pail(fs, path);
        assertPailContents(pail, FriendFacts.d1, FriendFacts.d2, FriendFacts.d3);
    }

}

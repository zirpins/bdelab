package de.hska.iwi.bdelab.batchstore;

import static com.backtype.support.TestUtils.assertPailContents;

import org.apache.hadoop.fs.FileSystem;
import org.junit.Before;
import org.junit.Test;

import com.backtype.hadoop.RenameMode;
import com.backtype.hadoop.pail.Pail;

import de.hska.iwi.bdelab.schema.Data;
import manning.tap.DataPailStructure;

public class FactsOpsTest {
    private final boolean LOCAL = true;

    private FileSystem fs;

    @Before
    public void setUp() throws Exception {
        fs = FileUtils.getFs(LOCAL);
    }

    @Test
    public void basicAppendTest() throws Exception {
        String path1 = FileUtils.getTmpPath(fs, "pail", true, LOCAL);
        String path2 = FileUtils.getTmpPath(fs, "pail2", true, LOCAL);
        // test non structured append
        Pail<Data> p1 = Pail.create(fs, path1, new DataPailStructure());
        Pail<Data> p2 = Pail.create(fs, path2, new DataPailStructure());

        Pail<Data>.TypedRecordOutputStream os = p1.openWrite();
        os.writeObject(FriendFacts.d1);
        os.writeObject(FriendFacts.d2);
        os.close();

        os = p2.openWrite();
        os.writeObject(FriendFacts.d3);
        os.close();

        p1.absorb(p2, RenameMode.RENAME_IF_NECESSARY);

        assertPailContents(p1, FriendFacts.d1, FriendFacts.d2, FriendFacts.d3);
    }

}

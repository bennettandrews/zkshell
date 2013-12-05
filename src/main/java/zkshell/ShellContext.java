package zkshell;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ShellContext {

    private String currentZNode = "/";

    private ZooKeeper zooKeeper;

    public ShellContext(String zkservers) throws IOException {
        connect(zkservers);
    }

    public void setCurrentZNode(String znode) {
        currentZNode = node;
    }

    public String getCurrentZNode() {
        return currentZNode;
    }

    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void connect(String string) throws IOException {
        zooKeeper = new ZooKeeper(string, 5000, null);
    }
}

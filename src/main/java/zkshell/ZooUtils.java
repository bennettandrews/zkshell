package zkshell;

import java.util.List;
import java.util.LinkedList;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

public class ZooUtils {
    
    public static boolean isAbsolutePath(String path) {
        return path.startsWith("/");
    }

    public static boolean isValidPath(ShellContext context, String path) {
        try {
			Stat stat = context.getZooKeeper().exists(path, false);
			if (stat == null)
				return false;
			return true;
		} 
		catch (KeeperException e) {} 
		catch (InterruptedException e) { }
		return false;
    }
    
    public static void printChildren(ShellContext context, String path) {
        List<String> children = getChildren(context,path);
        for (String c : children) {
            System.out.println(c);
        }
    }

    public static List<String> getChildren(ShellContext context, String path) {
        try {
            List<String> children = context.getZooKeeper().getChildren(path, false);
            return children;
        } 
        catch (KeeperException e) { }
        catch (InterruptedException e) { }
        return new LinkedList<String>();
    }
    


}
import java.util.ArrayList;

public class MemoryData {
    private int blockFreeSize=0,totalFreeSize=0;
    ArrayList<Integer> idArr = new ArrayList<Integer>();
    ArrayList<Integer> startAdd = new ArrayList<Integer>();
    ArrayList<Integer> endAdd = new ArrayList<Integer>();

    public int getBlockFreeSize() {
        return blockFreeSize;
    }

    public void setBlockFreeSize(int blockFreeSize) {
        this.blockFreeSize = blockFreeSize;
    }

    public int getTotalFreeSize() {
        return totalFreeSize;
    }

    public void setTotalFreeSize(int totalFreeSize) {
        this.totalFreeSize = totalFreeSize;
    }
}

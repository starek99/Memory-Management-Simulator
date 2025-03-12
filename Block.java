public class Block {
    
    private int StartAddress;
    private int endAddress;
    private int id;
    private boolean allocated;

    public Block(int startAddress, int endAddress, int id, boolean allocated) {
        StartAddress = startAddress;
        this.endAddress = endAddress;
        this.id = id;
        this.allocated = allocated;
    }

    public Block(int id){
        this.id = id;
    }
    public int getStartAddress() {
        return StartAddress;
    }

    public void setStartAddress(int startAddress) {
        StartAddress = startAddress;
    }

    public int getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(int endAddress) {
        this.endAddress = endAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

}

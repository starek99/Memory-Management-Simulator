public class History {
    String command;
    private int thirdP;
    private int errorLine;

    public History(String command, int errorLine,int thirdP) {
        this.command = command;
        this.thirdP = thirdP;
        this.errorLine = errorLine;
    }


    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getThirdP() {
        return thirdP;
    }

    public void setThirdP(int thirdP) {
        this.thirdP = thirdP;
    }

    public int getErrorLine() {
        return errorLine;
    }

    public void setErrorLine(int errorLine) {
        this.errorLine = errorLine;
    }
}

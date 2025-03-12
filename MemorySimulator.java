


import javax.sound.midi.Soundbank;
import java.io.*;
import java.util.ArrayList;
import java.util.SortedMap;
//import java.util.Collections;
//import java.util.Comparator;


public class MemorySimulator {
    
    int MemorySize = 0;

    private Block[] firstBlk;
    private Block[] worstBlk;
    private Block[] bestBlk;

    private MemoryData first = new MemoryData();
    private MemoryData worst = new MemoryData();
    private MemoryData best = new MemoryData();


    ArrayList<Integer> prevIds =new ArrayList<Integer>();

    ArrayList<History> firstErr = new ArrayList<History>();
    ArrayList<History> bestErr = new ArrayList<History>();
    ArrayList<History> worstErr = new ArrayList<History>();

    public Block[] getFirstBlk() {
        return firstBlk;
    }

    public Block[] getBestBlk() {
        return bestBlk;
    }

    public Block[] getWorstBlk() {
        return worstBlk;
    }

    int co=0,nCo=1;
    public void Run(String inputFile) throws Exception{

        String finalOutput=inputFile.replace(".in", ".out");
        String outputFile = inputFile.replace(".in", ".out");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(finalOutput));

        String line,out;
        while((line = reader.readLine()) != null) {

            String[] tokens = line.split(";");
            if (tokens[0].equals("A")) {

                int id =Integer.parseInt(tokens[1]);
                int size = Integer.parseInt(tokens[2]);
                prevIds.add(id);
                MemoryAllocate(id,size);
            }
            else if (tokens[0].equals("D")) {

                int id =Integer.parseInt(tokens[1]);

                if (MemoryDeallocate(id,firstBlk,first)) {
                } else {

                   if (errorType(id)){firstErr.add(new History("D",co,1));}
                    else {firstErr.add(new History("D",co,0));}
                }
                if (MemoryDeallocate(id,bestBlk,best)) {

                } else {
                    if (errorType(id)){bestErr.add(new History("D",co,1));}
                    else {bestErr.add(new History("D",co,0));}
                }
                if (MemoryDeallocate(id,worstBlk,worst)) {

                } else {
                    if (errorType(id)){
                        worstErr.add(new History("D",co,1));
                    }
                    else {
                        worstErr.add(new History("D",co,0));
                    }
                }
            } else if (tokens[0].equals("O")) {
                String temp=outputFile+nCo;
                BufferedWriter writer2 = new BufferedWriter(new FileWriter(temp,true));
                Display(writer2,"First Fit",firstBlk,first, firstErr);
                Display(writer2,"Best Fit",bestBlk,best, bestErr);
                Display(writer2,"Worst Fit",worstBlk,worst, worstErr);
                nCo++;
                writer2.close();
            }
            else if(tokens[0].equals("C")){
                compact(firstBlk,first);
                compact(bestBlk,best);
                compact(worstBlk,worst);
            }
            else{
                MemorySize=Integer.parseInt(tokens[0]);

                firstBlk= new Block[MemorySize];
                createMemory(MemorySize, firstBlk);
                first.setTotalFreeSize(MemorySize);
                first.setBlockFreeSize(MemorySize);

                bestBlk=new Block[MemorySize];
                createMemory(MemorySize, bestBlk);
                best.setTotalFreeSize(MemorySize);
                best.setBlockFreeSize(MemorySize);

                worstBlk=new Block[MemorySize];
                createMemory(MemorySize, worstBlk);
                worst.setTotalFreeSize(MemorySize);
                worst.setBlockFreeSize(MemorySize);

            }
            co++;
        }

        Display(writer,"First Fit",firstBlk,first,firstErr);
        Display(writer,"Best Fit",bestBlk,best,bestErr);
        Display(writer,"Worst Fit",worstBlk,worst,worstErr);
        reader.close();
        writer.close();

    }
    private void Display(BufferedWriter writer,String name,Block[] blk,MemoryData temp,ArrayList<History> err) throws IOException {


            String out;
            double frag = 0;
            writer.write("\n"+name);
            writer.write("\nAllocated blocks");
            for (int i = 0; i < temp.idArr.size(); i++) {
                out = String.valueOf(temp.idArr.get(i)) + ";" + String.valueOf(temp.startAdd.get(i) + ";" + String.valueOf(temp.endAdd.get(i)));
                writer.write("\n" + out);
            }
            writer.write("\nFree blocks");
            for (int i = 0; i < blk.length; i++) {
                if (blk[i].isAllocated() == false) {
                    for (int j = i + 1; j < blk.length; j++) {

                        if (blk[j].isAllocated() == true) {
                            out = String.valueOf(i) + ";" + String.valueOf(j - 1);
                            writer.write("\n" + out);
                            i = j;
                            break;
                        }
                        if (j == blk.length - 1) {
                            out = String.valueOf(i) + ";" + String.valueOf(j);
                            writer.write("\n" + out);
                            i = j;
                            break;
                        }
                    }
                }
            }
            writer.write("\nFragmentation");
        //System.out.println("\nsssssssssssssssssssss "+temp.getBlockFreeSize()+" "+ temp.getTotalFreeSize());
            frag = 1.0 - (double) temp.getBlockFreeSize() / temp.getTotalFreeSize();
            out = String.format("%.6f", frag);
            writer.write("\n" + out);
            writer.write("\nErrors");
            if (err.size() != 0) {
                for (int i = 0; i < err.size(); i++) {

                    out = err.get(i).getCommand() + ";" + String.valueOf(err.get(i).getErrorLine()) + ";" + String.valueOf(err.get(i).getThirdP());
                    writer.write("\n" + out);

                }
            } else {
                writer.write("\nNone");
            }


        writer.newLine();
        writer.newLine();
        writer.newLine();

    }
    
    private void MemoryAllocate(int id,int size){
        

            if(BestFit(id,size)==true){
                best.setTotalFreeSize(best.getTotalFreeSize()-size);
            }
            else{
                bestErr.add(new History("A",co, best.getBlockFreeSize()));
            }

            if(WorstFit(id,size)==true){
                worst.setTotalFreeSize(worst.getTotalFreeSize()-size);
            }
            else{
                worstErr.add(new History("A",co, worst.getBlockFreeSize()));
            }

            if(FirstFit(id,size)==true){
                first.setTotalFreeSize(first.getTotalFreeSize()-size);
            }
            else{
                firstErr.add(new History("A",co, first.getBlockFreeSize()));
            }

    }
    
    private boolean BestFit(int id,int size){
        int ind = -1,mx=MemorySize+5;
        boolean found = false;
        int co2=0;
        for(int i =0;i<bestBlk.length;i++){
            if(bestBlk[i].getId()==-1)
                co2++;
            else{

                if(co2<mx && co2>=size){
                    mx = co2;
                    ind = i - co2;
                    found=true;
                }
                co2=0;
            }
            if(i== bestBlk.length-1){
                if(co2<mx && co2>=size){
                    mx = co2;
                    ind = i - co2 + 1;
                    found=true;
                }
            }
        }
        int co = 0;
        if(found){

            best.idArr.add(id);
            best.startAdd.add(ind);
            best.endAdd.add(ind+size-1);

            for(int i = ind ;i<bestBlk.length;i++){
                bestBlk[i].setAllocated(true);
                bestBlk[i].setId(id);
                bestBlk[i].setStartAddress(ind);
                bestBlk[i].setEndAddress((ind+size-1));

                co++;
                if(co==size)
                    break;
            }
            int c=0,mx2=-1;
            boolean found2= false;
            for (int i=0;i<bestBlk.length;i++){

                if(bestBlk[i].isAllocated()==true){
                    if(c> mx2){
                        mx2=c;
                    }
                    c=0;
                }
                else {
                    c++;
                }
                if(i == bestBlk.length -1&&c>mx2) {
                    best.setBlockFreeSize(c);
                    found2=true;
                }
            }
            if (found2==false) {
                best.setBlockFreeSize(mx2);
            }
            System.out.println("\nBest " + best.getBlockFreeSize());
            return true;
        }
        else {
            return false;
        }
    }

    private boolean WorstFit(int id,int size){
        int ind = -1,mx=-1;
        boolean found = false;
        int co2=0;
        for(int i =0;i<worstBlk.length;i++){
            if(worstBlk[i].getId()==-1)
                co2++;
            else{
                if(co2>mx && co2>=size){

                    mx = co2;
                    ind = i - co2;
                    found=true;
                }
                co2=0;
            }

            if(i== worstBlk.length-1){
                if(co2>mx && co2>=size){

                    mx = co2;
                    ind = i - co2 + 1;
                    found=true;

                }
            }
        }
        int co = 0;
        if(found){
            worst.idArr.add(id);
            worst.startAdd.add(ind);
            worst.endAdd.add(ind+size-1);
            for(int i = ind ;i<worstBlk.length;i++){
                worstBlk[i].setAllocated(true);
                worstBlk[i].setId(id);
                worstBlk[i].setStartAddress(ind);
                worstBlk[i].setEndAddress((ind+size-1));
                co++;
                if(co==size)
                    break;
            }
            int c=0,mx2=-1;
            boolean found2= false;
            for (int i=0;i<worstBlk.length;i++){

                if(worstBlk[i].isAllocated()==true){
                    if(c> mx2){
                        mx2=c;
                    }
                    c=0;
                }
                else {
                    c++;
                }
                if(i == worstBlk.length -1&&c>mx2) {
                    worst.setBlockFreeSize(c);
                    found2=true;
                }
            }
            if (found2==false) {
                worst.setBlockFreeSize(mx2);
            }
            System.out.println("\nWorst " + worst.getBlockFreeSize());
            return true;
        }
        else {
            return false;
        }
    }

    private boolean FirstFit(int id,int size){
        String out;
        boolean found=false;
        int co=0;
        int ind=-1;
        for (int i=0;i< firstBlk.length;i++){
            if(co==size){
                found=true;
                ind=i-co;
                break;
            }
            co++;
            if(firstBlk[i].isAllocated()==true){

                co=0;
            }
        }
        if(co>=size && found==false){
            ind = firstBlk.length - co;
            found=true;
            System.out.println("hgygy");
        }
        int co2 = 0;
        if(found){
            first.idArr.add(id);
            first.startAdd.add(ind);
            first.endAdd.add(ind+size-1);
            for(int i = ind ;i<firstBlk.length;i++){
                firstBlk[i].setAllocated(true);
                firstBlk[i].setId(id);
                firstBlk[i].setStartAddress(ind);
                firstBlk[i].setEndAddress((ind+size-1));
                co2++;
                if(co2==size)
                    break;
            }
            int c=0,mx2=-1;
            boolean found2= false;
            for (int i=0;i<firstBlk.length;i++){

                if(firstBlk[i].isAllocated()==true){
                    if(c> mx2){
                        mx2=c;
                    }
                    c=0;
                }
                else {
                    c++;
                }
                if(i == firstBlk.length -1&&c>mx2) {
                    first.setBlockFreeSize(c);
                    found2=true;
                }
            }
            if (found2==false) {
                first.setBlockFreeSize(mx2);
            }

            return true;
        }
        else {
            return false;
        }
        
    }

    private boolean MemoryDeallocate(int id,Block[] blk,MemoryData temp){
        boolean found=false;

        for(int i = 0;i<temp.idArr.size();i++){
            if(temp.idArr.get(i)== id) {
                found=true;
                temp.setTotalFreeSize(temp.getTotalFreeSize()+(temp.endAdd.get(i)-temp.startAdd.get(i)+1));
                temp.idArr.remove(i);
                temp.startAdd.remove(i);
                temp.endAdd.remove(i);
                break;
            }

        }
        for (int i=0;i<blk.length;i++) {
            if (blk[i].getId() == id) {
                blk[i].setAllocated(false);
                blk[i].setId(-1);
                blk[i].setStartAddress(-1);
                blk[i].setEndAddress(-1);

            }
        }
        int c=0,mx2=-1;
        boolean found2= false;
        for (int i=0;i<blk.length;i++){

            if(blk[i].isAllocated()==true){
                if(c> mx2){
                    mx2=c;
                }
                c=0;
            }
            else {
                c++;
            }
            if(i == blk.length -1 && c>mx2) {
                temp.setBlockFreeSize(c);
                found2=true;
            }
        }
        if (found2==false) {
            temp.setBlockFreeSize(mx2);
        }

        System.out.println("\n DeAllocate "+temp.getBlockFreeSize());
        if(found){
            return true;
        }
        else
       return false;
    }

    public void compact(Block[] blk,MemoryData temp){
        for(int i=0;i<blk.length;i++){
            if(blk[i].isAllocated() == false) {
                int idxAlloc=-1;
                for (int j = i; j < blk.length; j++) {
                    if(blk[j].isAllocated() == true){
                        idxAlloc = j;
                        break;
                    }
                }
                if(idxAlloc == -1)
                    break;
                int freeIdx = i ,co = 0, sizeOfAlloc = blk[idxAlloc].getEndAddress() -  blk[idxAlloc].getStartAddress() + 1;
                for(int j = idxAlloc;j<blk.length;j++){
                    co++;
                    blk[freeIdx] = blk[j];
                    blk[freeIdx].setStartAddress(i);
                    blk[freeIdx].setEndAddress(sizeOfAlloc + i -1);
                    blk[j] = new Block(-1,-1,-1,false);
                    freeIdx++;
                    if(co == sizeOfAlloc)
                        break;
                }
                for(int j = 0;j<temp.idArr.size();j++){
                    if(temp.idArr.get(j)== blk[i].getId()) {
                        temp.startAdd.set(j,blk[i].getStartAddress());
                        temp.endAdd.set(j,blk[i].getEndAddress());
                        break;
                    }
                }
            }
        }
        temp.setBlockFreeSize(temp.getTotalFreeSize());
    }

    public void createMemory(int MemorySize,Block[] blk){
        for(int i = 0;i<MemorySize;i++) {
            blk[i] = new Block(-1, -1, -1, false);
        }
    }

    public boolean errorType(int id){
        boolean found=false;
        for(int i=0;i<prevIds.size();i++){
            if(id==prevIds.get(i)){
                found=true;
                break;
            }
        }
        if (found){
           return true;
        }
        else {
            return false;
        }
    }

}



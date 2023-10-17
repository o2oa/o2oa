package test.executor;

public class Worker implements Runnable {
    
    private int ID;
     
    public Worker(int id){
        this.ID = id;
     }
 
    @Override
    public void run() {
         try{
                Thread curThread = Thread.currentThread();
                System.out.println(curThread.getName() + " currently executing the task " + ID);
                Thread.sleep(500);
                System.out.println(curThread.getName() + " just completed the task " + ID);
             }
             catch(Exception e){
                System.out.println(e);
             }
         
    }
 
    public int getID() {
        return ID;
    }
 
    public void setID(int iD) {
        ID = iD;
    }
}
public class Bee implements Runnable{
    Bear bear;
    Pot pot;
    Bee nextBee;
    int numberOfBee;
    volatile boolean isActive = false;
    Bee (int numberOfBee, Bear bear, Pot pot){
        this.numberOfBee = numberOfBee;
        this.bear = bear;
        this.pot = pot;
    }
    public void setNextBee(Bee bee){
        this.nextBee = bee;
    }
    public void setActive(){
        isActive = true;
    }
    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("\uD83D\uDC1D #" + numberOfBee + " + 1 \uD83D\uDCA7");
            pot.addHoney();
            if (pot.isFull()) {
                System.out.println("\uD83C\uDF6F is full");
                bear.wakeUp();
            }
            isActive = false;
        }
    }
}

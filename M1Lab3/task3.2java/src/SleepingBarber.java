import java.util.concurrent.*;

public class SleepingBarber extends Thread {

    public static Semaphore customers = new Semaphore(0);
    public static Semaphore barber = new Semaphore(0);
    public static Semaphore accessSeats = new Semaphore(1);

    public static final int CHAIRS = 5;

    public static int numberOfFreeSeats = CHAIRS;
    class Customer extends Thread {

        int iD;
        boolean notCut = true;

        public Customer(int i) {
            iD = i;
        }

        public void run() {
                try {
                    accessSeats.acquire();
                    if (numberOfFreeSeats > 0) {
                        numberOfFreeSeats--;
                        System.out.println("Customer " + this.iD + " just sat down.(" + numberOfFreeSeats + " free \uD83D\uDCBA)");
                        customers.release();
                        accessSeats.release();
                        try {
                            barber.acquire();
                            notCut = false;
                            this.get_haircut();
                        } catch (InterruptedException ex) {}
                    }
                    else  {
                        System.out.println("There are no free \uD83D\uDCBA. Customer " + this.iD + " has left the barbershop.");
                        accessSeats.release();
                        notCut = false;
                    }
                }
                catch (InterruptedException ex) {}
        }

        public void get_haircut(){
            System.out.println("Customer " + this.iD + " is getting ✂");
            try {
                sleep(5050);
            } catch (InterruptedException ex) {}
        }

    }

    class Barber extends Thread {

        public Barber() {}

        public void run() {
            while(true) {
                try {
                    customers.acquire();
                    accessSeats.acquire();
                    numberOfFreeSeats++;
                    barber.release();
                    accessSeats.release();
                    this.cutHair();
                } catch (InterruptedException ex) {}
            }
        }

        public void cutHair(){
            System.out.println("The barber is cutting hair");
            try {
                sleep(5000);
            } catch (InterruptedException ex){ }
        }
    }

    public static void main(String args[]) {

        SleepingBarber barberShop = new SleepingBarber();
        barberShop.start();
    }

    public void run(){
        Barber barber = new Barber();
        barber.start();

        for (int i=1; i<16; i++) {
            Customer aCustomer = new Customer(i);
            aCustomer.start();
            try {
                sleep(2000);
            } catch(InterruptedException ex) {};
        }
    }
}
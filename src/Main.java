import java.util.*;

public class Main {

    private static boolean isFinished = false;
    public static ArrayList<Page> ram = new ArrayList<>();
    private static Queue<Integer> freeFrames = new LinkedList<>();
    private static Map<Integer, Process> processMap = new HashMap<>();

    public static void main(String[] args) { startMainInput(); }

    private static void startMainInput() {
        Scanner scanner = new Scanner(System.in);

        while(!isFinished) {
            System.out.println("\n***MAIN MENU***");
            //1. RAM Mode
            //2. Process Mode
            //3. Translate Mode
            //4. Quit
            System.out.println("Enter Mode:\n" +
                    "1. RAM\n" +
                    "2. Process\n" +
                    "3. Translate\n" +
                    "4. Exit Program");
            System.out.print("cmd: ");
            int modeSelection = scanner.nextInt();

            switch (modeSelection) {
                case 1:
                    memoryMode();
                    break;
                case 2:
                    processMode();
                    break;
                case 3:
                    translateMode();
                    break;
                case 4:
                    isFinished = true;
                    System.exit(0);
                    break;
                default:
                    System.out.println("Error.");
                    break;
            }
        }
    }

    private static void memoryMode() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n***MANAGE RAM***");
        //1. Set Frame Capacity
        //2. Status
        //3. Main Menu
        //4. Exit
        System.out.println("Enter Command:\n" +
                "1. Set frame capacity\n" +
                "2. Status\n" +
                "3. Main Menu\n" +
                "4. Exit");
        System.out.print("cmd: ");
        int memoryModeCommand = scanner.nextInt();

        switch (memoryModeCommand) {
            case 1:
                System.out.print("Enter frame capacity: ");
                createRAM(scanner.nextInt());
                break;
            case 2:
                status();
                break;
            case 3:
                startMainInput();
                break;
            case 4:
                isFinished = true;
                System.exit(0);
                break;
            default:
                System.out.println("Error");
                break;
        }
    }

    private static void createRAM(int numberOfFrames) {
        for (int i = 0; i < numberOfFrames; i++) {
            ram.add(new Page());
            freeFrames.add(i);
        }
    }

    private static void status() {
        System.out.println("Frame\tPage #\tPID");
        for (int i = 0; i < ram.size(); i++) {
            System.out.printf("%d\t\t%d\t\t%d\n", i, ram.get(i).pageID, ram.get(i).pid);
        }
    }

    private static void processMode() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n***MANAGE PROCESSES***");
        //1. Create Process
        //2. Finish Process
        //3. Show Process Page Table
        //4. Main Menu
        //5. Exit
        System.out.println("Enter Command:\n" +
                "1. Create Process\n" +
                "2. Finish Process\n" +
                "3. Show Process Page Table\n" +
                "4. Main Menu\n" +
                "5. Exit Program");
        System.out.print("cmd: ");
        int processModeCommand = scanner.nextInt();

        switch (processModeCommand) {
            case 1:
                System.out.print("Enter PID: ");
                int pid = scanner.nextInt();
                System.out.print("Enter Number of Pages: ");
                int numberOfPages = scanner.nextInt();
                createProcess(pid, numberOfPages);
                break;
            case 2:
                System.out.print("Enter PID: ");
                finishProcess(scanner.nextInt());
                break;
            case 3:
                System.out.print("Enter PID: ");
                printPageTable(scanner.nextInt());
                break;
            case 4:
                startMainInput();
                break;
            case 5:
                isFinished = true;
                System.exit(0);
                break;
            default:
                System.out.println("Error.");
                break;
        }
    }

    private static void createProcess(int pid, int numberOfPages) {
        if (numberOfPages > freeFrames.size()) {
            System.out.println("Error. Not enough space.");
        } else {
            Process newProcess = new Process(pid, numberOfPages);
            processMap.put(pid, newProcess);
            ArrayList<Page> pageTable = newProcess.getPageTable();
            for (int i = 0; i < numberOfPages; i++) {
                pageTable.get(i).frame = freeFrames.remove();
            }
        }
    }

    private static void finishProcess(int pid) {
        Process process = processMap.remove(pid);
        for (int i = 0; i < process.getPageTable().size(); i++) {
            ram.add(new Page());
            freeFrames.add(process.getPageTable().get(i).frame);
        }
    }

    private static void printPageTable(int pid) {
        Process process = processMap.get(pid);
        System.out.println("Page\tFrame");
        for (int i = 0; i < process.getPageTable().size(); i++) {
            System.out.printf("%d\t\t%d\n", i, process.getPageTable().get(i).frame);
        }
    }

    private static void translateMode() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter PID: ");
        int inputPid = scanner.nextInt();
        System.out.print("Enter Logical Address (Page Number): ");
        int inputLogicalAddress = scanner.nextInt();
        int translatedAddress = translateAddress(inputPid, inputLogicalAddress);
        System.out.printf("Page %d of process %d is at frame %d", inputLogicalAddress, inputPid, translatedAddress);
    }

    private static int translateAddress(int pid, int pageNumber) {
        Process process = processMap.get(pid);
        return process.getPageTable().get(pageNumber).frame;
    }
}

class Process {
    private int pid;
    private ArrayList<Page> pageTable = new ArrayList<>();

    public Process(int pid, int numberOfPages) {
        this.pid = pid;
        createPages(numberOfPages);
    }

    private void createPages(int numberOfPages) {
        for (int i = 0; i < numberOfPages; i++) {
            Page newPage = new Page(pid, i);
            pageTable.add(newPage);
            Main.ram.add(newPage);
        }
    }

    public ArrayList<Page> getPageTable() { return pageTable; }
}

class Page {
    protected int frame;
    protected int pid;
    protected int pageID;

    public Page() {}

    public Page(int pid, int pageID) {
        this.pid = pid;
        this.pageID = pageID;
    }
}
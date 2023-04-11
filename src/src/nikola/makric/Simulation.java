package nikola.makric;

import java.util.Scanner;

public class Simulation {
    public static void main(String []args)
    {
        Scanner scanner = new Scanner(System.in);
        int brojOkvira;int brojReferenci;
        System.out.print("Unesite broj okvira:");
        brojOkvira = Integer.parseInt(scanner.nextLine());
        System.out.print("Unesite broj referenci:");
        brojReferenci = Integer.parseInt(scanner.nextLine());
        int [] reference = new int[brojReferenci];
        String stringReference = "";
        System.out.print("Unesite reference:");
        stringReference = scanner.nextLine();
        var splitReference = stringReference.split(",");
        try {
            if (splitReference.length != brojReferenci)
                throw new Exception("Nije unijet tacan broj referenci");
        }catch(Exception e)
        {
            System.out.println(e);
            return;
        }
        for(int i=0;i<splitReference.length;i++)
        {
            try{
                String trimmedReference = splitReference[i].trim();
                reference[i] = Integer.parseInt(trimmedReference);
            }catch(NumberFormatException e)
            {
                System.out.println(e);
                return;
            }
        }
        String algorithms;
        System.out.print("Izaberite algoritme:");
        algorithms = scanner.nextLine();
        var algorithmArray = algorithms.split(",");
        for(int i=0;i<algorithmArray.length;i++)
        {
            String trimmedAlgorithm = algorithmArray[i].trim();
            algorithmArray[i] = trimmedAlgorithm;
        }
        MemoryManagmentUnit mmu = new MemoryManagmentUnit(algorithmArray,reference,brojOkvira);
        scanner.close();
        mmu.Simulate();
        //1,2,3,4,2,1,5,6,2,1,2,3,7,6,3,2,1,2,3,6 FIFO,LRU,Second Chance,LFU,Optimalni
    }
}

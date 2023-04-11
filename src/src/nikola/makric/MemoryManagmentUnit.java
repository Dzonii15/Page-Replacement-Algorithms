package nikola.makric;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MemoryManagmentUnit {
    String[] listaAlgoritama;
    int[] reference;
    int brojOkvira;

    public MemoryManagmentUnit(String[] listaAlgoritama, int[] reference, int brojOkvira) {
        this.listaAlgoritama = listaAlgoritama;
        this.reference = reference;
        this.brojOkvira = brojOkvira;
    }

    public void Simulate() {
        for (String algoritam : listaAlgoritama) {
            if ("FIFO".equals(algoritam)) {
                System.out.println("-=FIFO=-");
                this.fifoAlgorithm();
            } else if ("LRU".equals(algoritam)) {
                System.out.println("-=LRU=-");
                this.LRUAlgorithm();
            } else if ("Second Chance".equals(algoritam)) {
                System.out.println("-=Second Chance=-");
                Set<Integer> rBits = new HashSet<>();
                String answer;
                Scanner scanner = new Scanner(System.in);
                scanner.nextInt();
                System.out.print("Da li zelite ubaciti R bitove?(y/n)");
                answer = scanner.nextLine();
                if ("y".equals(answer)) {
                    int brojBitova;
                    System.out.print("Unesite za koliko stranica zelite R bitove:");
                    brojBitova = scanner.nextInt();
                    for (int i = 0; i < brojBitova; i++) {
                        System.out.print(i + ".stranica kojoj zelite dodijeliti Rbit:");
                        rBits.add(Integer.parseInt(scanner.nextLine()));
                    }
                }
                scanner.close();
                this.SecondChanceAlgorithm(rBits);
            } else if ("LFU".equals(algoritam)) {
                System.out.println("-=LFU=-");
                Scanner scanner = new Scanner(System.in);
                int initalCounter;
                int counterDecrease;
                int counterIncrease;
                System.out.print("Unesite pocetnu vrijednost brojaca:");
                initalCounter = Integer.parseInt(scanner.nextLine());
                System.out.print("Unesite vrijednost za koju ce se povecavati brojac:");
                counterIncrease = Integer.parseInt(scanner.nextLine());
                System.out.print("Unesite vrijednost za koju ce se smanjivati brojac:");
                counterDecrease = Integer.parseInt(scanner.nextLine());
                scanner.close();
                this.LFUAlgorithm(initalCounter, counterIncrease, counterDecrease);
            } else if ("Optimalni".equals(algoritam)) {
                System.out.println("-=Optimalni=-");
                this.optimalAlgorithm();
            } else {
                System.out.println("Nepoznata opcija");
            }
            System.out.println();
        }
    }

    public void fifoAlgorithm() {
        int pfCounter = 0;
        String[] pageFault = new String[reference.length];
        int[][] matricaAlgoritma = new int[brojOkvira + 1][reference.length];
        this.initializeSimulationBegining(pageFault, matricaAlgoritma);
        for (int i = 0; i < reference.length; i++) {
            int referenca = matricaAlgoritma[0][i];
            //prekopiraj prethodno stanje
            this.prekopirajUNovuKolonu(i, matricaAlgoritma);
            if (daLiJeUMemoriji(referenca, matricaAlgoritma, i)) {
                //nista se ne desava ostaje situacija
            } else {
                //nije ucitana u memoriju pageFault i vrsi se pomjeranje svi dole jedan nivo
                this.pomjeranjeDoleSaPF(i, referenca, matricaAlgoritma, pageFault);
                pfCounter++;
            }
        }
        this.ispisSimulacije(pageFault, matricaAlgoritma,pfCounter);
    }

    public void LRUAlgorithm() {
        int pfCounter = 0;
        String[] pageFault = new String[reference.length];
        int[][] matricaAlgoritma = new int[brojOkvira + 1][reference.length];
        this.initializeSimulationBegining(pageFault, matricaAlgoritma);
        for (int i = 0; i < reference.length; i++) {
            int referenca = matricaAlgoritma[0][i];
            //prekopiraj prethodno stanje
            this.prekopirajUNovuKolonu(i, matricaAlgoritma);
            if (daLiJeUMemoriji(referenca, matricaAlgoritma, i)) {
                this.zamijeniStraniceULRU(referenca, i, matricaAlgoritma);

            } else {
                //nije ucitana u memoriju pageFault i vrsi se pomjeranje svi dole jedan nivo
                this.pomjeranjeDoleSaPF(i, referenca, matricaAlgoritma, pageFault);
                pfCounter++;
            }
        }
        this.ispisSimulacije(pageFault, matricaAlgoritma,pfCounter);
    }

    public void SecondChanceAlgorithm(Set<Integer> straniceSaRBitom) {
        int pfCounter = 0;
        Set<Integer> iskoristenBit = new HashSet<>(straniceSaRBitom);
        String[] pageFault = new String[reference.length];
        int[][] matricaAlgoritma = new int[brojOkvira + 1][reference.length];
        this.initializeSimulationBegining(pageFault, matricaAlgoritma);
        for (int i = 0; i < reference.length; i++) {
            int referenca = matricaAlgoritma[0][i];
            //prekopiraj prethodno stanje
            this.prekopirajUNovuKolonu(i, matricaAlgoritma);
            if (daLiJeUMemoriji(referenca, matricaAlgoritma, i)) {
                //ako je vec ucitana onda nista ne diramo


            } else {
                //nije ucitana u memoriju pageFault i vrsi se pomjeranje svi dole jedan nivo
                if (straniceSaRBitom.contains(matricaAlgoritma[brojOkvira][i])) {
                    int brojSaRbitom = matricaAlgoritma[brojOkvira][i];
                    if (iskoristenBit.contains(brojSaRbitom)) {
                        this.zamijeniStraniceULRU(brojSaRbitom, i, matricaAlgoritma);
                        this.pomjeranjeDoleSaPF(i, referenca, matricaAlgoritma, pageFault);

                        iskoristenBit.remove(brojSaRbitom);
                    } else {

                        this.pomjeranjeDoleSaPF(i, referenca, matricaAlgoritma, pageFault);
                        iskoristenBit.add(brojSaRbitom);
                    }

                } else {

                    this.pomjeranjeDoleSaPF(i, referenca, matricaAlgoritma, pageFault);

                }

            }
        }
        pfCounter++;
        this.ispisSimulacije(pageFault, matricaAlgoritma,pfCounter);
    }

    public void LFUAlgorithm(int initialCounter, int counterIncrease, int counterDecrease) {
        int pfCounter = 0;
        int[] counterTracker = new int[brojOkvira];
        Arrays.fill(counterTracker, -1);
        String[] pageFault = new String[reference.length];
        int[][] matricaAlgoritma = new int[brojOkvira + 1][reference.length];
        this.initializeSimulationBegining(pageFault, matricaAlgoritma);
        for (int i = 0; i < reference.length; i++) {
            int referenca = matricaAlgoritma[0][i];
            //prekopiraj prethodno stanje
            this.prekopirajUNovuKolonu(i, matricaAlgoritma);
            if (daLiJeUMemoriji(referenca, matricaAlgoritma, i)) {
                //ako je u memoriji unaprijediti brojace
                int indeks = 0;
                for (int j = 1; j < brojOkvira + 1; j++) {
                    if (matricaAlgoritma[j][i] == referenca)
                        indeks = j - 1;
                    else
                        counterTracker[j - 1] -= counterDecrease;
                }
                counterTracker[indeks] += counterIncrease;
                int f = indeks;
                while (f > 0 && counterTracker[f] >= counterTracker[f - 1]) {
                    int zaZamjenu = counterTracker[f - 1];
                    counterTracker[f - 1] = counterTracker[f];
                    counterTracker[f] = zaZamjenu;
                    zaZamjenu = matricaAlgoritma[f][i];
                    matricaAlgoritma[f][i] = matricaAlgoritma[f + 1][i];
                    matricaAlgoritma[f + 1][i] = zaZamjenu;
                    f--;
                }

            } else {
                //ubacujem novi
                //on ce imati inital value
                //svi ostali se decrease
                for (int j = 0; j < counterTracker.length; j++)
                    counterTracker[j] -= counterDecrease;
                int brojac = brojOkvira - 1;
                while (brojac > 0 && counterTracker[brojac - 1] <= initialCounter) {
                    counterTracker[brojac] = counterTracker[brojac - 1];
                    matricaAlgoritma[brojac + 1][i] = matricaAlgoritma[brojac][i];
                    brojac--;
                }
                pageFault[i] = "PF";
                matricaAlgoritma[brojac + 1][i] = referenca;
                counterTracker[brojac] = initialCounter;
                pfCounter++;

            }
        }
        this.ispisSimulacije(pageFault, matricaAlgoritma,pfCounter);
    }

    public void optimalAlgorithm() {
        int pfCounter = 0;
        String[] pageFault = new String[reference.length];
        int[][] matricaAlgoritma = new int[brojOkvira + 1][reference.length];
        this.initializeSimulationBegining(pageFault, matricaAlgoritma);
        for (int i = 0; i < reference.length; i++) {
            int referenca = matricaAlgoritma[0][i];
            //prekopiraj prethodno stanje
            this.prekopirajUNovuKolonu(i, matricaAlgoritma);
            if (daLiJeUMemoriji(referenca, matricaAlgoritma, i)) {
                //nista se ne desava ostaje situacija
            } else {
                //look into future
                int secondaryCounter = 0;
                boolean daLiSuPrazne = false;
                for (int j = 1; j < brojOkvira + 1; j++) {
                    if (matricaAlgoritma[j][i] == -1) {
                        daLiSuPrazne = true;
                        secondaryCounter++;
                        break;
                    }
                    secondaryCounter++;
                }
                if (daLiSuPrazne) {
                    while (secondaryCounter > 1) {
                        matricaAlgoritma[secondaryCounter][i] = matricaAlgoritma[secondaryCounter - 1][i];
                        secondaryCounter--;
                    }
                    matricaAlgoritma[secondaryCounter][i] = referenca;
                } else {
                    int maksIndeksUReferencama = -1;
                    int tajIndeksUKoloni = -1;
                    for (int j = 0; j < brojOkvira + 1; j++) {
                        for (int k = i; k < reference.length; k++) {
                            if (matricaAlgoritma[j][i] == reference[k]) {
                                if (k > maksIndeksUReferencama) {
                                    maksIndeksUReferencama = k;
                                    tajIndeksUKoloni = j;
                                }
                                break;
                            }
                            if (k == reference.length - 1) {
                                maksIndeksUReferencama = k;
                                tajIndeksUKoloni = j;
                            }
                        }
                    }


                    matricaAlgoritma[tajIndeksUKoloni][i] = referenca;
                }
                pageFault[i] = "PF";
                pfCounter++;
            }
        }
        this.ispisSimulacije(pageFault, matricaAlgoritma, pfCounter);
    }

    private void pomjeranjeDole(int i, int referenca, int[][] matricaAlgoritma, int endIndex) {
        int tmp = referenca;

        for (int j = 1; j < endIndex; j++) {
            int tmp2 = matricaAlgoritma[j][i];
            matricaAlgoritma[j][i] = tmp;
            tmp = tmp2;
        }
    }


    private void initializeSimulationBegining(String[] pageFault, int[][] matricaAlgoritma) {
        System.arraycopy(reference, 0, matricaAlgoritma[0], 0, reference.length);
        for (int i = 1; i < brojOkvira + 1; i++)
            for (int j = 0; j < reference.length; j++)
                matricaAlgoritma[i][j] = -1;

    }

    boolean daLiJeUMemoriji(int referenca, int[][] matricaAlgortima, int kolonaPoKojojGledam) {
        for (int i = 1; i < brojOkvira + 1; i++)
            if (matricaAlgortima[i][kolonaPoKojojGledam] == referenca)
                return true;
        return false;
    }

    private void ispisSimulacije(String[] pageFault, int[][] matricaAlgoritma, int pfCounter) {
        for (int k : reference) System.out.print(String.format("%2d ", k));
        System.out.println();
        for (String pageFaults : pageFault)
            if (pageFaults != null)
                System.out.print(String.format("%2s ", pageFaults));
            else System.out.print(String.format("%2s ", ""));
        System.out.println();
        for (int i = 1; i < brojOkvira + 1; i++) {
            for (int j = 0; j < reference.length; j++)
                if (matricaAlgoritma[i][j] != -1)
                    System.out.print(String.format("%2d ", matricaAlgoritma[i][j]));
                else System.out.print(String.format("%2s ", ""));
            System.out.println();
        }
        System.out.println("Efikasnost algoritma PF:" + pfCounter + " => pf = " + (double) pfCounter / (double) reference.length * 100 + "%");
    }

    private void prekopirajUNovuKolonu(int i, int[][] matricaAlgoritma) {
        if (i != 0) {
            for (int j = 1; j < brojOkvira + 1; j++) {
                matricaAlgoritma[j][i] = matricaAlgoritma[j][i - 1];
            }
        }
    }

    private void pomjeranjeDoleSaPF(int i, int referenca, int[][] matricaAlgoritma, String[] pageFault) {
        pageFault[i] = "PF";
        this.pomjeranjeDole(i, referenca, matricaAlgoritma, brojOkvira + 1);
    }


    private void zamijeniStraniceULRU(int referenca, int kolona, int[][] matricaAlgoritma) {
        int indexPoVrsti = 0;
        for (int i = 1; i < brojOkvira + 1; i++) {
            if (matricaAlgoritma[i][kolona] == referenca) {
                indexPoVrsti = i;
                break;
            }
        }
        this.pomjeranjeDole(kolona, referenca, matricaAlgoritma, indexPoVrsti + 1);
    }

}

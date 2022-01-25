class BaseDeDados {
    public static int contador = 0;
}

class Monitor {
    private int leit, escr;  
    
    Monitor() { 
       this.leit = 0; 
       this.escr = 0;
    } 
    
    public synchronized void EntraLeitor (int id) {
      try { 
        while (this.escr > 0) {
           System.out.println ("Leitor bloqueado "+id);
           wait(); 
        }
        this.leit++;
      } catch (InterruptedException e) { }
    }
    
    public synchronized void SaiLeitor (int id) {
       this.leit--;
       System.out.println ("Leitor saindo "+id);
       if (this.leit == 0) 
            this.notify();
    }
    
    public synchronized void EntraEscritor (int id) {
      try { 
        while ((this.leit > 0) || (this.escr > 0)) {
           System.out.println("Escritor entrando " + id);
           wait();
        }
        this.escr++;
        System.out.println ("Escritor escrevendo " + id);
      } catch (InterruptedException e) { }
    }
    
    public synchronized void SaiEscritor (int id) {
       this.escr--;
       System.out.println ("Escritor saindo " + id);
       notifyAll();
    }
}
class Leitor extends Thread {
    int id; 
    int delay; 
    Monitor monitor;
  
    Leitor (int id, int delayTime, Monitor m) {
      this.id = id;
      this.delay = delayTime;
      this.monitor = m;
    }
  
    public void run () {
      try {
        for (;;) {
          this.monitor.EntraLeitor(this.id);
          int valor = BaseDeDados.contador;
          if(valor % 2 == 0) {
            System.out.println ("Leitor " + id + " leu " + valor + " que é par");
          } else {
            System.out.println ("Leitor " + id + " leu " + valor + " que é ímpar");
          }
          this.monitor.SaiLeitor(this.id);
          sleep(this.delay); 
        }
      } catch (InterruptedException e) { return; }
    }
}

class Escritor extends Thread {
    int id;
    int delay;
    Monitor monitor;
  
    Escritor (int id, int delayTime, Monitor m) {
      this.id = id;
      this.delay = delayTime;
      this.monitor = m;
    }
  
    public void run () {
      try {
        for (;;) {
          this.monitor.EntraLeitor(this.id);
          int valor = BaseDeDados.contador;
          if(valor % 2 == 0) {
            System.out.println ("LeitorEscritor " + id + " leu " + valor + " que é par");
          } else {
            System.out.println ("LeitorEscritor " + id + " leu " + valor + " que é ímpar");
          }
          this.monitor.SaiLeitor(this.id);
          for(int i =0; i<Integer.MAX_VALUE; i++) {
              if (i == Integer.MAX_VALUE / 2) {
                System.out.println ("LeitorEscritor " + id + " chegou na metade do processamento bobo");
              }
          }
          this.monitor.EntraEscritor(this.id);
          System.out.println ("LeitorEscritor " + id + " incrementou");
          BaseDeDados.contador++;
          this.monitor.SaiEscritor(this.id); 
          sleep(this.delay);
        }
      } catch (InterruptedException e) { return; }
    }
}

class LeitorEscritor extends Thread {
    int id;
    int delay;
    Monitor monitor;
  
    LeitorEscritor (int id, int delayTime, Monitor m) {
      this.id = id;
      this.delay = delayTime;
      this.monitor = m;
    }
  
    public void run () {
      try {
        for (;;) {
          this.monitor.EntraEscritor(this.id); 
          BaseDeDados.contador++;
          this.monitor.SaiEscritor(this.id); 
          sleep(this.delay);
        }
      } catch (InterruptedException e) { return; }
    }
}
 
class MainLeitoresEscritores {
    static final int L = 4;
    static final int E = 3;
    static final int LE = 6;
    public static void main (String[] args) {
      int i;
      Monitor monitor = new Monitor();
      Leitor[] l = new Leitor[L];
      Escritor[] e = new Escritor[E];
      LeitorEscritor[] le = new LeitorEscritor[LE];

      for (i=0; i<L; i++) {
         l[i] = new Leitor(i+1, (i+1)*500, monitor);
         l[i].start(); 
      }
      for (i=0; i<E; i++) {
         e[i] = new Escritor(i+1, (i+1)*500, monitor);
         e[i].start(); 
      }
      for (i=0; i<LE; i++) {
        le[i] = new LeitorEscritor(i+1, (i+1)*500, monitor);
        le[i].start(); 
     }

      try {
        Thread.sleep(10000);
      } catch(InterruptedException exception) {}
      System.exit(0);
    }
}
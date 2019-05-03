package Listas;

import Errores.DatoNoExistenteException;

import Errores.DatosUsadosException;
import Errores.EsquemaNuloException;
import Errores.TamanoException;
import sample.Server;


import java.util.Hashtable;

public class Esquema {
    private String nombre,ID;
    private Boolean tiene_filas;
    private ListaTables filas=new ListaTables();
    private ListaTamano tamanos=new ListaTamano();
    private ListaString mijoins =new ListaString();
    public ListaString joinde = new ListaString();

    public Esquema(String constructor) throws EsquemaNuloException {
        String[] partes=constructor.split(",");
        this.tiene_filas=false;
        this.nombre=partes[0];
        this.crearfila(partes);

    }
    private void crearfila(String[] partes) throws NumberFormatException, EsquemaNuloException {
        Hashtable fila=new Hashtable();
        int cont=1;
        this.ID=partes[cont].split(":")[0];
        while (cont<partes.length) {
            String nombre = partes[cont].split(":")[0];
            String tipo = partes[cont].split(":")[1];
            int tamano = Math.abs(Integer.parseInt(partes[cont].split(":")[2]));
            System.out.println(tipo);
            if (tipo.equals("STRING")) {
                fila.put(nombre, "");
            } else if (tipo.equals("INT")) {
                fila.put(nombre, -1);
            } else if (tipo.equals("DOUBLE")) {
                fila.put(nombre, (double) -1);
            } else if (tipo.equals("LONG")) {
                fila.put(nombre, (long) -1);
            } else if (tipo.equals("FLOAT")) {
                fila.put(nombre, (float) -1);
            }
            if ("STRING,INT,DOUBLE,LONG,FLOAT".contains(tipo)) {
                this.tamanos.addLast(new Tamano(nombre, tamano));
                System.out.println(tamanos.largo);
            }
            if (tipo.equals("JOIN")) {
                Esquema esquema = Server.esquemas.buscar(nombre);
                if (esquema==null){
                    throw new EsquemaNuloException();
                }
                else {
                    mijoins.addFirst(nombre);
                    esquema.joinde.addFirst(this.nombre);
                    System.out.println(mijoins.head.getNodo());
                    fila.put(nombre, esquema.filas.getHead().getNodo().get(esquema.getID()));
                }
            }
            cont++;
        }
        System.out.println(fila);
        this.filas.addLast(fila);
    }

    public void anadirfila(String fila) throws TamanoException, DatoNoExistenteException, NumberFormatException {
        Hashtable base= (Hashtable) this.filas.head.getNodo().clone();
        String[] datos= fila.split(",");
        int cont=0;
        while (cont<datos.length){
            String nombre=datos[cont].split(":")[0];
            String dato=datos[cont].split(":")[1];
            if (this.tamanos.contiene(nombre)) {
                System.out.println(this.tamanos.buscartamano(nombre));
                if (this.tamanos.buscartamano(nombre) >= dato.length()) {
                    System.out.println("voy a cambiar dato");
                    base.replace(nombre, this.filas.convertir(dato, nombre));
                }
                else {
                    throw new TamanoException();
                }
            }
            else if(this.mijoins.contiene(nombre)){
            Esquema esquema=Server.esquemas.buscar(nombre);
            System.out.println(esquema.getFilas().getHead().getNodo());
                if (esquema.existe(dato,esquema.getID())){
                    System.out.println("voy a cambiar dato en join");
                    base.replace(nombre,this.filas.convertir(dato,nombre));
                }
                else {
                    throw new DatoNoExistenteException(nombre);
                }
            }
            cont++;
        }
        if (!tiene_filas){
            this.filas.head.setNodo(base);
            tiene_filas=true;
        }
        else {this.filas.addLast(base);}
    }

    public void eliminarfila(String dato) throws DatosUsadosException {
        if (this.datousado(dato)){
            throw new DatosUsadosException();
        }
        else {
            if (this.filas.getLargo()>1){
            this.filas.eliminar(dato,this.ID);
            }
            else{
                this.tiene_filas=false;
            }
        }
    }

    public String buscardatos(String dato,String nombre)throws StringIndexOutOfBoundsException{
        String datos="";
        if (nombre.equals(this.ID)){
            if (this.filas.existe(dato,nombre)){datos = crearstring(dato,nombre);}
        }
        else {
            int cont=0;
            while (cont<this.filas.getLargo()){
                Hashtable fila=this.filas.buscar(cont);
                if (fila.get(nombre).toString().equals(dato)){
                    datos=datos.concat(this.crearstring(fila.get(this.ID).toString(),this.getID()));
                    datos=datos.concat(";");
                }
                cont++;
            }
            datos=datos.substring(0,datos.length()-1);
        }
        return datos;
    }
    public String buscardatosjoin(String join,String nombre,String dato) throws StringIndexOutOfBoundsException{//usado si el parametro de busqueda es por el de un dato en un join que no sea el ID
        Esquema esquema=Server.esquemas.buscar(join);
        String datos="";
        int cont=0;
        while (cont<this.filas.getLargo()){
            Hashtable fila=filas.buscar(cont);
            Hashtable filajoin=esquema.getFilas().buscar(fila.get(join).toString(),esquema.getID());
            if (filajoin.get(nombre).toString().equals(dato)){
                datos=datos.concat(this.crearstring(fila.get(this.ID).toString(),this.getID()));
                datos=datos.concat(";");
            }
            cont++;
        }
        datos=datos.substring(0,datos.length()-1);
        return datos;
    }

    public String buscartodos() throws EsquemaNuloException {
        String datos="";
        int cont=0;
        while (cont<this.filas.getLargo()){
            System.out.println("entro aca");
            Hashtable fila=this.filas.buscar(cont);
            datos=datos.concat(this.crearstring(fila.get(this.ID).toString(),this.getID()));
            datos=datos.concat(";");
            cont++;
        }
        datos=datos.substring(0,datos.length()-1);
        if (!this.tiene_filas){throw new EsquemaNuloException();}
        return datos;
    }

    private String crearstring(String dato,String nombre){
        Hashtable fila= this.filas.buscar(dato,nombre);
        String string=fila.toString();
        if (this.mijoins.getLargo()!=0){
            int cont=0;
            while (cont<mijoins.getLargo()){
                Esquema esquema=Server.esquemas.buscar(mijoins.buscar(cont));
                string=string.concat(":"+esquema.getNombre()+"=");
                string=string.concat(esquema.buscardatos(fila.get(mijoins.buscar(cont)).toString(),esquema.getID()));
                cont++;
            }
        }
        return string;
    }

    public Boolean existe(String dato,String nombre){
        return this.filas.existe(dato,nombre);
    }

    public Boolean existejoin(){
        Boolean existe =false;
        if (this.joinde.getLargo()>0){existe=true;}
        return existe;
    }
    private Boolean datousado(String dato){
        Boolean usado=false;
        int cont=0;
        while (cont< joinde.largo) {
            if (Server.esquemas.buscar(joinde.buscar(cont)).existe(dato,this.nombre)){
                usado=true;
                break;
            }
            cont++;
        }
        return usado;
    }



    public String getNombre() {
        return nombre;
    }

    public Boolean getTiene_filas() {
        return tiene_filas;
    }

    public ListaTables getFilas() {
        return filas;
    }

    public ListaTamano getTamanos() {
        return tamanos;
    }

    public ListaString getMijoins() {
        return mijoins;
    }

    public String getID() {
        return ID;
    }

    public ListaString getJoinde() {
        return joinde;
    }

    public void setJoinde(ListaString joinde) {
        this.joinde = joinde;
    }
}

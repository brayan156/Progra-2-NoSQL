package Listas;

import java.util.HashSet;
public class Lista<t>{
	public NodoL<t> first;
	public int largo;
	
	public NodoL<t> getNodo() {
		return first;
	}
	public Lista() {
		this.first=null;
		largo=0;
	}
	public void addlist(t dato,String nombre) {
		if (first==null) {
			first=new NodoL<t>(dato,nombre);
		}else {
			NodoL<t> temp=first;
			while (temp.getNext()!=null) {
				temp=temp.getNext();
				}
			temp.next=new NodoL<t>(dato,nombre);
		}
		largo++;
	}
	public boolean deleteNode(String dato) {  
		if (this.first.nombre.equals(dato)){
			this.first=this.first.next;
            		largo--;
            		return true;
		}else{//Se le pone el nombre de la columna que se desea eliminar de la lista arboles
			NodoL<t>tmp=this.first;
			while (tmp.next!=null){
				if (tmp.next.nombre.equals(dato)){
					tmp.next=tmp.next.next;
                    			largo--;
                    			return true;
				}else {
					tmp=tmp.next;
				}
			}return false;
		}
	}
	public t Search(String nombre) {//metodo para buscar los indices por sus nombre de columna(key)
		int cont=0;
		NodoL<t> temp=first;
		while (cont<largo) {
			if (temp.nombre.equals(nombre)) {
				return temp.getNodo();
			}else {
				cont++;
				temp=temp.next;
			}
		}
		return null;
	}
	public boolean verDupl(ListaTables filas,String key){  
		HashSet<t> s=new HashSet<>(); 
		return Duplicado(filas, s, key);  
	}  
	private boolean Duplicado(ListaTables filas, HashSet<t> s,String key) {
		int parada=filas.getLargo();
		for (int i=0;parada>i;i++) {
			if (s.contains(filas.buscar(i).get(key).toString())){// me mete en un Hashset los elementos de la columna
				return false;//y si lo vuelve a encontrar me da false
			}s.add((t) filas.buscar(i).get(key).toString());
		}return true;
	}
	
	public class NodoL<T>{
	    private T nodo=null;
	    public int num;
	    public String nombre;
	    public NodoL <T> next=null;
	    
	    public NodoL(T nodo,String nombre){
	        this.nodo= nodo;
	        this.next= null;
	        this.nombre=nombre;
	    }
	    public T getNodo() {
	        return nodo;
	    }
	    public void setNodo(T nodo) {
	        this.nodo = nodo;
	    }
	    public NodoL<T> getNext() {
	        return next;
	    }
	    public void setNext(NodoL<T> next) {
	        this.next = next;
	    }
	}
}

package Listas;
import java.util.Hashtable;

import Arboles.AVL;
import Arboles.ArbolAA;
import Arboles.ArbolB;
import Arboles.ArbolBPlus;
import Arboles.ArbolBinario;
import Arboles.ArbolRB;
public class Indice<t> {
	private String nombre;
	private ArbolAA AA;
	private ArbolRB RB;
	private ArbolB B;
	private ArbolBPlus BPlus;
	private ArbolBinario Binario;
	private AVL AVL;
	
	public Indice(String nombre,NombreArbol dato) {
		this.nombre=nombre;
		if (dato==NombreArbol.ArbolAA) {
			AA=new ArbolAA();
		}else if(dato==NombreArbol.ArbolRB) {
			RB=new ArbolRB();
		}else if(dato==NombreArbol.ArbolB) {
			B=new ArbolB();
		}else if(dato==NombreArbol.ArbolBPlus) {
			BPlus=new ArbolBPlus();
		}else if(dato==NombreArbol.ArbolBinario) {
			Binario=new ArbolBinario();
		}else {
			AVL=new AVL();
		}
	}
	public String getNombre() {
		return nombre;
	}
	public ArbolAA getAA() {
		return AA;
	}
	public ArbolRB getRB() {
		return RB;
	}
	public ArbolB getB() {
		return B;
	}
	public ArbolBPlus getBPlus() {
		return BPlus;
	}
	public ArbolBinario getBinario() {
		return Binario;
	}
	public AVL getAVL() {
		return AVL;
	}
}
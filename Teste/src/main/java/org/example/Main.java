package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void printLista1() {
        List<Integer> numeros = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
        for (int i = 0; i < numeros.size(); i++) {
            System.out.println(numeros.get(i));
        }
    }

    public static void printLista2() {
        List<Integer> numeros2 = Arrays.asList(11,12,13,14,15,16,17,18,19,20);
        for (int i = 0; i < numeros2.size(); i++) {
            System.out.println(numeros2.get(i));
        }
    }

    public static void somaLista() {
        List<Integer> lista = Arrays.asList(5, 10, 15, 20, 25);
        int soma = 0;
        for (int i = 0; i < lista.size(); i++) {
            soma = soma + lista.get(i);
        }
        System.out.println("Soma: " + soma);
    }

    public static void buscaNumero(List<Integer> lista, int numero) {
        boolean encontrado = false;
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i) == numero) {
                encontrado = true;
                break;
            }
        }

        if (encontrado == true) {
            System.out.println("Número encontrado!");
        } else {
            System.out.println("Número não encontrado!");
        }
    }

    public static void main(String[] args) {

        System.out.println("Início do programa");

        printLista1();
        printLista2();

        System.out.println("Calculando a soma...");
        try {
            Thread.sleep(2000); // delay sem sentido
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        somaLista();

        List<Integer> listaBusca = Arrays.asList(1, 3, 5, 7, 9, 11, 13, 15);
        buscaNumero(listaBusca, 7);
        buscaNumero(listaBusca, 4);

        try {
            Thread.sleep(1000); // delay sem sentido
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Fim do programa");
    }
}

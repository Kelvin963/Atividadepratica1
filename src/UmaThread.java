import java.io.*;
import java.util.regex.*;
import java.util.*;
import java.nio.file.*;

public class UmaThread {

    public static void main(String[] args) {
        Runnable tarefa = () -> {
            try {
                long inicio = System.currentTimeMillis(); // Início da medição de tempo

                // Chama a função para analisar a pasta
                analisarPasta("C:\\Users\\kelvin.sousa\\Documents\\JavaProjeto\\JavaProjeto\\cpfs");

                long fim = System.currentTimeMillis(); // Fim da medição de tempo
                long tempoTotal = fim - inicio; // Tempo total de execução

                // Salva os resultados no arquivo
                salvarResultados(tempoTotal);

            } catch (IOException erro) {
                System.out.println("Falha em arquivo: " + erro.getMessage());
            }
        };

        Thread processoUnico = new Thread(tarefa);
        processoUnico.start();
    }

    public static void analisarPasta(String caminho) throws IOException {
        File pasta = new File(caminho);
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("O Diretorio eh invalido.");
            return;
        }

        File[] listaArquivos = pasta.listFiles((dir, nome) -> nome.endsWith(".txt"));
        if (listaArquivos == null) return;

        int validos = 0;
        int invalidos = 0;

        for (File atual : listaArquivos) {
            List<String> conteudo = Files.readAllLines(atual.toPath());
            for (String linha : conteudo) {
                List<String> encontrados = localizarCPF(linha);
                for (String documento : encontrados) {
                    if (cpfVerificacao(documento)) {
                        validos++;
                    } else {
                        invalidos++;
                    }
                }
            }
        }

        // Salva os resultados no arquivo
        salvarResultados(validos, invalidos);
    }

    public static List<String> localizarCPF(String entrada) {
        List<String> resultado = new ArrayList<>();
        Pattern padrao = Pattern.compile("(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2})");
        Matcher busca = padrao.matcher(entrada);

        while (busca.find()) {
            String extraido = busca.group().replaceAll("\\D", "");
            resultado.add(extraido);
        }

        return resultado;
    }

    public static boolean cpfVerificacao(String num) {
        if (num.length() != 11 || num.chars().distinct().count() == 1) {
            return false;
        }
    
        int primeiroDigito = calcularDigitoVerificador(num, 10, 9);
    
        int segundoDigito = calcularDigitoVerificador(num, 11, 10);
   
        return primeiroDigito == (num.charAt(9) - '0') && segundoDigito == (num.charAt(10) - '0');
    }
    
    private static int calcularDigitoVerificador(String num, int pesoFinal, int limite) {
        int soma = 0;
    
        for (int i = 0; i < limite; i++) {
            soma += (num.charAt(i) - '0') * (pesoFinal - i);
        }
    
        int verificador = (soma * 10) % 11;
        if (verificador == 10) {
            verificador = 0;
        }
    
        return verificador;
    }

    public static void salvarResultados(int validos, int invalidos) throws IOException {
        String pastaCaminho = "C:\\Users\\kelvin.sousa\\Documents\\JavaProjeto\\JavaProjeto\\resultados\\";

        // Cria a pasta se não existir
        File pasta = new File(pastaCaminho);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        // Nome do arquivo
        String arquivoCaminho = pastaCaminho + "versao_1_thread.txt";

        // Criação e escrita do arquivo com os resultados
        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoCaminho));
        writer.write("Válidos: " + validos + "\n");
        writer.write("Inválidos: " + invalidos + "\n");

        writer.close();
        System.out.println("Resultados salvos em: " + arquivoCaminho);
    }

    public static void salvarResultados(long tempoTotal) throws IOException {
        String pastaCaminho = "C:\\Users\\kelvin.sousa\\Documents\\JavaProjeto\\JavaProjeto\\resultados\\";

        // Cria a pasta se não existir
        File pasta = new File(pastaCaminho);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        // Nome do arquivo
        String arquivoCaminho = pastaCaminho + "versao_1_thread.txt";

        // Criação e escrita do arquivo com o tempo total de execução
        BufferedWriter writer = new BufferedWriter(new FileWriter(arquivoCaminho, true));
        writer.write("Tempo total em milissegundos gasto : " + tempoTotal);
        writer.close();
    
        System.out.println("Tempo total de execução salvo em: " + arquivoCaminho);
    }
}

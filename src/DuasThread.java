import java.io.*;
import java.util.regex.*;
import java.util.*;
import java.nio.file.*;

public class DuasThread {

    public static void main(String[] args) {
        Runnable tarefaInicial = () -> {
            try {
                long tempoInicio = System.currentTimeMillis(); 

                // Processa a primeira parte dos arquivos
                processarDiretorio("C:\\Users\\kelvin.sousa\\Documents\\JavaProjeto\\JavaProjeto\\cpfs", 0, 15);

                long tempoFinal = System.currentTimeMillis(); 
                long duracaoTotal = tempoFinal - tempoInicio; 

                // Chama a função para registrar o tempo total de todas as threads juntas
                registrarTempo(duracaoTotal);

            } catch (IOException erro) {
                System.out.println("Falha ao ler arquivos: " + erro.getMessage());
            }
        };

        Runnable tarefaSecundaria = () -> {
            try {
                long tempoInicio = System.currentTimeMillis(); 

                // Processa a segunda parte dos arquivos
                processarDiretorio("C:\\Users\\kelvin.sousa\\Documents\\JavaProjeto\\JavaProjeto\\cpfs", 15, 30);

                long tempoFinal = System.currentTimeMillis(); 
                long duracaoTotal = tempoFinal - tempoInicio; 

                // Chama a função para registrar o tempo total de todas as threads juntas
                registrarTempo(duracaoTotal);

            } catch (IOException erro) {
                System.out.println("Falha ao ler arquivos: " + erro.getMessage());
            }
        };

        Thread threadPrimeira = new Thread(tarefaInicial);
        Thread threadSegunda = new Thread(tarefaSecundaria);

        threadPrimeira.start();
        threadSegunda.start();
    }

    static int validos = 0;
    static int invalidos = 0;

    public static void processarDiretorio(String caminho, int inicio, int fim) throws IOException {
        File pasta = new File(caminho);
        if (!pasta.exists() || !pasta.isDirectory()) {
            System.out.println("Diretório incorreto!");
            return;
        }

        File[] arquivos = pasta.listFiles((dir, nome) -> nome.endsWith(".txt"));
        if (arquivos == null) return;

        for (int i = inicio; i < fim; i++) {
            if (i >= arquivos.length) break;
            File arquivo = arquivos[i];
            List<String> linhas = Files.readAllLines(arquivo.toPath());
            for (String linha : linhas) {
                List<String> cpfsExtraidos = extrairNumeros(linha);
                for (String cpf : cpfsExtraidos) {
                    if (validarNumero(cpf)) {
                        validos++;
                    } else {
                        invalidos++;
                    }
                }
            }
        }
    }

    public static List<String> extrairNumeros(String texto) {
        List<String> numeros = new ArrayList<>();
        Pattern padrao = Pattern.compile("(\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2})");
        Matcher correspondencia = padrao.matcher(texto);

        while (correspondencia.find()) {
            String numeroLimpo = correspondencia.group().replaceAll("\\D", "");
            numeros.add(numeroLimpo);
        }

        return numeros;
    }

    public static boolean validarNumero(String numero) {
        if (numero.length() != 11 || numero.chars().distinct().count() == 1) {
            return false;
        }

        int primeiroDigito = calcularDigitoVerificador(numero, 10, 9);
        int segundoDigito = calcularDigitoVerificador(numero, 11, 10);

        return primeiroDigito == (numero.charAt(9) - '0') && segundoDigito == (numero.charAt(10) - '0');
    }

    private static int calcularDigitoVerificador(String numero, int pesoFinal, int limite) {
        int soma = 0;

        for (int i = 0; i < limite; i++) {
            soma += (numero.charAt(i) - '0') * (pesoFinal - i);
        }

        int verificador = (soma * 10) % 11;
        return (verificador == 10) ? 0 : verificador;
    }

    // Função modificada para registrar o número de CPFs válidos, inválidos e o tempo total de todas as threads em milissegundos
    public static void registrarTempo(long tempo) throws IOException {
        String caminhoPasta = "C:\\Users\\kelvin.sousa\\Documents\\JavaProjeto\\JavaProjeto\\resultados\\";

        File pasta = new File(caminhoPasta);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        String caminhoArquivo = caminhoPasta + "versao_2_threads.txt";

        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(caminhoArquivo, true))) {
            escritor.write("Válidos: " + validos + "\n");
            escritor.write("Inválidos: " + invalidos + "\n");
            escritor.write("Tempo gasto em milissegundos ao todo: " + tempo + "\n");
        }

        System.out.println("Tempo total registrado em: " + caminhoArquivo);
    }
}

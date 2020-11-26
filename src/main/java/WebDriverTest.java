import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.*;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import java.text.SimpleDateFormat;
import java.util.*;

public class WebDriverTest {

    public static void main(String[] args) throws InterruptedException, FindFailed {

        // instanciando:

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Scanner sc = new Scanner (System.in);

        Date today = new Date();

        String todayStr = sdf.format(today);

        List<String> troListCreated = new ArrayList<>();

        String password = "xxx";

        String user = "xxx";

        String processo = "50520.009852/2020-13";

        int resp = 0;

       while( resp != 1 && resp != 2 && resp != 3) {
           System.out.println("1 - Criar e Colar os TROs e Anexos\n" +
                   "2 - somente Colar Tros e Anexos \n" +
                   "3 - liberar para Assinatura");

           resp = sc.nextInt();
           sc.nextLine();
           if (resp == 2) {
               System.out.print("Digite o numero do primeiro TRO : ");
               int primeiroTro = sc.nextInt();
               sc.nextLine();

               System.out.print("Digite o numero do ultimo TRO : ");
               int ultimoTro = sc.nextInt();
               sc.nextLine();

               List<String> troListParaColar = new ArrayList<>();

               for (int i = primeiroTro; i <= ultimoTro; i++) {
                   troListParaColar.add(Integer.toString(i));
               }

               WebDriver driver = openBrowserAndProcesso(user, password, processo);

               colarTrosEAnexos(driver, troListParaColar, password);

               System.exit(0);
           } else if (resp == 3) {

               System.out.print("Digite o numero do primeiro TRO : ");
               int primeiroTro = sc.nextInt();
               sc.nextLine();

               System.out.print("Digite o numero do ultimo TRO : ");
               int ultimoTro = sc.nextInt();
               sc.nextLine();

               List<String> troListParaColar = new ArrayList<>();

               for (int i = primeiroTro; i <= ultimoTro; i++) {
                   troListParaColar.add(Integer.toString(i));
               }

               WebDriver driver = openBrowserAndProcesso(user, password, processo);
               WebDriverWait wait = new WebDriverWait(driver, 30);

               liberarParaAssinatura(driver, wait, troListParaColar);
               System.exit(0);

           } else if (resp == 1) {
               System.out.print("Quantos TROs serão registrados? ");

               int vezes = sc.nextInt();
               sc.nextLine();

               WebDriver driver = openBrowserAndProcesso(user, password, processo);

               String mainWindow = driver.getWindowHandle();
               WebDriverWait wait = new WebDriverWait(driver, 30);

               for (int i = 1; i <= vezes; i++) {

                   driver.switchTo().defaultContent();

                   switchToFrame(driver, "ifrArvore");

                   waitToBeClickableAndClickById(driver, wait, "topmenu");

                   Thread.sleep(500);

                   waitToBeClickableAndClickById(driver, wait, "topmenu");

                   driver.switchTo().defaultContent();
                   wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifrVisualizacao")));


                   wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[src='imagens/sei_incluir_documento.gif']")));
                   driver.findElement(By.cssSelector("[src='imagens/sei_incluir_documento.gif']")).click();

                   wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-desc='tro - suinf']")));

                   driver.findElement(By.cssSelector("[data-desc='tro - suinf']")).click();

                   waitAndClickById(driver, wait, "lblProtocoloDocumentoTextoBase");
                   driver.findElement(By.id("txtProtocoloDocumentoTextoBase")).sendKeys("2418322");

                   driver.findElement(By.id("lblPublico")).click();
                   driver.findElement(By.id("btnSalvar")).click();

                   closeAllPopupWindows(driver, mainWindow);

                   System.out.println("Fim Criação Tro");

                   System.out.println("Inicio Criação Anexo Em Branco");

                   switchToFrame(driver, "ifrArvore");

                   waitToBeClickableAndClickById(driver, wait, "topmenu");
                   driver.findElement(By.id("topmenu")).click();

                   Thread.sleep(500);

                   expandTree(driver);

                   List<String> spanText = new ArrayList<String>();

                   List<WebElement> we = driver.findElements(By.xpath("//span[text()[contains(.,'TRO - SUINF')]]"));
                   for (WebElement w : we) {
                       spanText.add(w.getText());
                   }

                   String troStr = spanText.get(spanText.size() - 1).substring(12, 16).trim();

                   troListCreated.add(troStr);

                   // clicar processo

                   driver.findElement(By.id("topmenu")).click();

                   Thread.sleep(3000);

                   driver.switchTo().defaultContent();

                   driver.switchTo().frame(1);

                   //clica em novo documento
                   driver.findElement(By.cssSelector("[src='imagens/sei_incluir_documento.gif']")).click();

                   //clica em externo

                   driver.findElement(By.cssSelector("[data-desc=' externo']")).click();

                   // seleciona externo

                   driver.findElement(By.id("selSerie")).click();
                   driver.findElement(By.cssSelector("[value='263']")).click();

                   driver.findElement(By.id("txtDataElaboracao")).sendKeys(todayStr);

                   driver.findElement(By.id("txtNumero")).sendKeys("TRO " + troStr + "/2020");

                   driver.findElement(By.id("lblNato")).click();

                   // selecionar publico

                   driver.findElement(By.id("lblPublico")).click();

                   driver.findElement(By.id("btnSalvar")).click();

                   // fim criação TRO e Anexo Em Branco
               }
               colarTrosEAnexos(driver, troListCreated, password);
           } else {
               System.out.println("tente novamente");
           }
       }
    }

    public static WebDriver openBrowserAndProcesso(String user, String password, String processo) throws InterruptedException {

        System.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        System.out.println("abrindo chrome");

        driver.get("https://sei.antt.gov.br/");
        System.out.println("abrindo pagina sei");

        WebElement usuario = driver.findElement(By.name("txtUsuario"));
        WebElement senha = driver.findElement(By.id("pwdSenha"));
        WebElement entrar = driver.findElement(By.id("sbmLogin"));

        usuario.sendKeys(user);
        senha.sendKeys(password);
        System.out.println("entrando com senha");

        entrar.click();

        WebElement pesquisa = driver.findElement(By.id("txtPesquisaRapida"));
        pesquisa.sendKeys(processo + "\n");
        System.out.println("entrando com numero processo");

        return driver;
    }

    public static void colarTrosEAnexos(WebDriver driver, List<String> troListCreated, String password ) throws InterruptedException {

        Scanner scan = new Scanner(System.in);

        Screen s = new Screen();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        System.out.println("Favor preencher os TROs abaixo no Infra:");

        for (String l : troListCreated) {
            System.out.println(l);
        }

        System.out.println("após registrar os TROs no Infra, tecle: '1' :");
        int resp = scan.nextInt();
        while (resp != 1) {
            System.out.print("pronto ? ");
            resp = scan.nextInt();
        }
        scan.nextLine();

        for (String tro : troListCreated) {

            // colar tro

            driver.switchTo().defaultContent();

            String mainWindow = driver.getWindowHandle();

            switchToFrame(driver, "ifrArvore");

            waitToBeClickableAndClickById(driver, wait, "topmenu");
            driver.findElement(By.id("topmenu")).click();
            Thread.sleep(500);

            expandTree(driver);

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()[contains(.,'TRO - SUINF " + tro + "')]]")));

            driver.findElement(By.xpath("//span[text()[contains(.,'TRO - SUINF " + tro + "')]]")).click();

            driver.switchTo().defaultContent();

            switchToFrame(driver, "ifrVisualizacao");

            driver.findElement(By.cssSelector("[src='imagens/sei_editar_conteudo.gif']")).click();

            Thread.sleep(3000);

            // change window

            String secondWindow = "";

            for (String windowHandle : driver.getWindowHandles()) {
                if (!mainWindow.contentEquals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    secondWindow = driver.getWindowHandle();
                    break;
                }
            }

            driver.switchTo().defaultContent();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("iframe[title='Editor de Rich Text, txaEditor_1931']")));
            driver.switchTo().frame(driver.findElement(By.cssSelector("iframe[title='Editor de Rich Text, txaEditor_1931']")));

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='Texto_Justificado_Recuo_Primeira_Linha']")));
            driver.findElement(By.cssSelector("[class='Texto_Justificado_Recuo_Primeira_Linha']")).click();

            driver.switchTo().defaultContent();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cke_109")));
            driver.findElement(By.id("cke_109")).click();

            //By finding list of the web elements using frame or iframe tag
            Thread.sleep(5000);

            List<WebElement> f = driver.findElements(By.tagName("iframe"));
            System.out.println("Total number " + f.size());

            List<WebElement> framesList = driver.findElements(By.xpath("//iframe"));
            int numOfFrames = framesList.size();
            System.out.println("numero frames= " + numOfFrames);

            for (int i = 0; i < f.size(); i++) {
                driver.switchTo().defaultContent();
                driver.switchTo().frame(i);
                System.out.println("fora if i = " + i);
                if (driver.findElements(By.id("cke_213_fileInput_input")).size() != 0) {
                    System.out.println("size = " + driver.findElements(By.id("cke_213_fileInput_input")).size());
                    System.out.println("entrou no if ni = " + i);

                    driver.switchTo().defaultContent();
                    driver.switchTo().frame(i);
                    WebElement fileInput = driver.findElement(By.id("cke_213_fileInput_input"));
                    fileInput.sendKeys("C:\\TROs.E.AIs\\TRO-" + tro + "-2020-COINFRS-SUINF.jpg");
                    Thread.sleep(5000);
                }
            }

            System.out.println("apertando botão ok");

            Pattern OkButton = new Pattern("E:\\QA.png");

            for (String windowHandle : driver.getWindowHandles()) {
                if (!mainWindow.contentEquals(windowHandle) && !secondWindow.contentEquals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            try {
                s.click(OkButton);
            } catch (FindFailed findFailed) {
                System.out.println("não foi localizado o botão Ok");
                findFailed.printStackTrace();
            }

            //Salvar
            driver.switchTo().defaultContent();

            Thread.sleep(1000);

            waitToBeClickableAndClickById(driver, wait, "cke_78");

            Thread.sleep(2000);

            //Assinar
            waitToBeClickableAndClickById(driver, wait, "cke_80");

            // pegar a terceira janela
            for (String windowHandle : driver.getWindowHandles()) {
                if (!mainWindow.contentEquals(windowHandle) && !secondWindow.contentEquals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            driver.findElement(By.id("pwdSenha")).sendKeys(password);
            waitAndClickById(driver, wait, "btnAssinar");

            driver.switchTo().window(mainWindow);

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifrArvore")));

            driver.findElement(By.xpath("//span[text()[contains(.,'Anexo TRO " + tro + "' )]]")).click();
            System.out.println("pegou no primeiro");

            driver.switchTo().defaultContent();

            wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifrVisualizacao")));

            driver.findElement(By.cssSelector("[title='Consultar/Alterar Documento Externo']")).click();

            WebElement fileInput = driver.findElement(By.id("filArquivo"));
            fileInput.sendKeys("C:\\Users\\elg10.DESKTOP-E8CTNI7\\OneDrive - ANTT- Agencia Nacional de Transportes Terrestres\\CRO\\Relatorios RTA\\" + tro + ".pdf");

            System.out.println("Aguarda upload Arquivo");

            driver.switchTo().defaultContent();
            driver.switchTo().frame("ifrVisualizacao");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[class='infraTrAcessada']")));
            waitAndClickById(driver, wait, "btnSalvar");

        }
    }

    private static void waitAndClickById(WebDriver driver, WebDriverWait wait, String id) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id)));
        driver.findElement(By.id(id)).click();
    }

    private static void waitToBeClickableAndClickById(WebDriver driver, WebDriverWait wait, String id) {
        wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
        driver.findElement(By.id(id)).click();
    }

    private static void switchToFrame(WebDriver driver, String frameName) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        driver.switchTo().defaultContent();
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(frameName)));
    }

    private static void expandTree(WebDriver driver) throws InterruptedException {
        System.out.print("Expandindo todas as pastas");

        Thread.sleep(3000);

        List<WebElement> span;
        span = driver.findElements(By.cssSelector("[id*='spanPASTA'"));
        if (span.size() > 0) {
            span.remove(span.size() - 1);
        }
        WebDriverWait wait = new WebDriverWait(driver, 30);
        for (WebElement e : span) {

            wait.until(ExpectedConditions.elementToBeClickable(e));
            e.click();
        }
        System.out.print("....feito !");
    }

    private static void closeAllPopupWindows(WebDriver driver, String mainWindow) {
        for (String windowHandle : driver.getWindowHandles()) {
            if (!mainWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                driver.close();
            }
        }
        driver.switchTo().window(mainWindow);
    }

    private static void liberarParaAssinatura(WebDriver driver, WebDriverWait wait, List<String> troList) throws InterruptedException {

        String mainWindow = driver.getWindowHandle();

        expandTree(driver);

        for(String tro: troList) {

            driver.switchTo().defaultContent();
            switchToFrame(driver, "ifrArvore");

            waitToBeClickableAndClickById(driver, wait, "topmenu");
            driver.findElement(By.id("topmenu")).click();
            Thread.sleep(500);

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()[contains(.,'TRO - SUINF " + tro + "')]]")));

            driver.findElement(By.xpath("//span[text()[contains(.,'TRO - SUINF " + tro + "')]]")).click();

            driver.switchTo().defaultContent();

            switchToFrame(driver, "ifrVisualizacao");

            driver.findElement(By.cssSelector("[src='imagens/sei_gerenciar_assinatura_externa.gif']")).click();

            Thread.sleep(3000);

            waitAndClickById(driver, wait, "selEmailUnidade");

            driver.findElement(By.cssSelector("[value='ANTT/E-MAIL DA UNIDADE <COINFRS@ANTT.GOV.BR>']")).click();

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.getElementById('hdnIdUsuario').setAttribute('value', '1000055')");

            waitAndClickById(driver, wait, "imgLupaProtocolos");

            Thread.sleep(3000);

            for (String windowHandle : driver.getWindowHandles()) {
                if (driver.findElements(By.id("hdnInfraNroItens")).size() > 0) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            for (String windowHandle : driver.getWindowHandles()) {
                if (!mainWindow.equals(windowHandle)) {
                    driver.switchTo().window(windowHandle);
                    System.out.println("entrou no segundo");
                    break;
                }
            }

            WebElement table = driver.findElement(By.className("infraTable"));

            List<WebElement> allrows = table.findElements(By.tagName("tr"));
            int rowNumber = -1 ;
            boolean flag = false;
            for (WebElement row : allrows) {
                if(flag){
                    break;
                }
                int cellNumber = 0;
                List<WebElement> cells = row.findElements(By.tagName("td"));

                for (WebElement cell : cells) {

                    if (cellNumber == 2) {
                        if (cell.getText().contains("Anexo TRO " + tro)) {
                            flag = true;
                            js.executeScript("infraTransportarItem("+ rowNumber +",'Infra')");
                            break;
                        }
                    }
                    cellNumber++;
                }
                rowNumber++;
            }
            System.out.println(rowNumber);

            driver.switchTo().window(mainWindow);
            switchToFrame(driver, "ifrVisualizacao");

            waitAndClickById(driver, wait, "btnLiberar");
        }
            driver.switchTo().window(mainWindow);
    }
}


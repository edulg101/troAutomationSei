import org.openqa.selenium.By;
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
        Scanner sc = new Scanner(System.in);

        System.out.print("Quantos TROs serão registrados? ");
        int vezes = sc.nextInt();

        System.out.print("nome usuario: ");
        String user = sc.next();

        System.out.print("senha: ");
        String password = sc.next();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Date today = new Date();

        String todayStr = sdf.format(today);

        List<String> troListCreated = new ArrayList<>();


        System.setProperty("webdriver.chrome.driver", "E:\\chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        WebDriverWait wait = new WebDriverWait(driver, 30);

        Screen s = new Screen();

        int test = 782;

        String mainWindow = driver.getWindowHandle();

        String secondWindow = "";

        int troNumber = 0;

        //fim estanciação

        System.out.println("abrindo chrome");

        driver.get("https://sei.antt.gov.br/");
        System.out.println("abrindo pagina sei");

        WebElement usuario = driver.findElement(By.name("txtUsuario"));
        WebElement senha = driver.findElement(By.id("pwdSenha"));
        WebElement entrar = driver.findElement(By.id("sbmLogin"));


        usuario.sendKeys(user);
        senha.sendKeys(password);
        System.out.println("entrando com senha");


        // para clicar no submit do formulario sei
        entrar.click();

        WebElement pesquisa = driver.findElement(By.id("txtPesquisaRapida"));
        pesquisa.sendKeys("50520.009852/2020-13" + "\n");
        System.out.println("entrando com numero processo");


        for (int i = 1; i <= vezes; i++) {

            // Criar TRO

           driver.switchTo().defaultContent();

           switchToFrame(driver, "ifrArvore");

            waitToBeClickableAndClickById(driver, wait, "topmenu");
            try {
                driver.findElement(By.id("topmenu")).click();
                System.out.println("primeiro try");
            }
            catch (org.openqa.selenium.StaleElementReferenceException e) {
                driver.findElement(By.id("topmenu")).click();
                System.out.println("entrou no catch");
            }

            Thread.sleep(500);

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
                System.out.println(w.getText());
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
//        select.selectByValue("263");
            driver.findElement(By.cssSelector("[value='263']")).click();

            driver.findElement(By.id("txtDataElaboracao")).sendKeys(todayStr);

            driver.findElement(By.id("txtNumero")).sendKeys("TRO " + troStr + "/2020");

            driver.findElement(By.id("lblNato")).click();

            // selecionar publico

            driver.findElement(By.id("lblPublico")).click();

            driver.findElement(By.id("btnSalvar")).click();

            // fim criação TRO e Anexo Em Branco

        }

        System.out.println("Favor preencher os TROs abaixo no Infra:");

        for (String l: troListCreated){
            System.out.println(l);
        }

        System.out.print("após registrar os TROs no Infra, tecle: 'SIM' :");
        String resp = sc.next();
        resp = resp.toLowerCase();
        while (!resp.equals("sim")) {
            System.out.print("pronto ? ");
            resp = sc.next();
            resp = resp.toLowerCase();
        }


        for (String tro : troListCreated) {

            // anexar imagem do TRO Correspondente

//            driver.switchTo().defaultContent();
//            switchToFrame(driver, "ifrArvore");
//
//
//            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()[contains(.,'TRO - SUINF "+ tro +"')]]")));
//
//            driver.findElement(By.xpath("//span[text()[contains(.,'TRO - SUINF "+ tro +"')]]")).click();
//
//
//            driver.switchTo().defaultContent();
//
//            switchToFrame(driver, "ifrVisualizacao");
//
//            wait.until(ExpectedConditions.titleIs("Consultar/Alterar Documento Externo"));
//            driver.findElement(By.cssSelector("[title='Consultar/Alterar Documento Externo']")).click();
//
//            WebElement fileInput = driver.findElement(By.id("filArquivo"));
//            fileInput.sendKeys("C:\\Users\\elg10.DESKTOP-E8CTNI7\\OneDrive - ANTT- Agencia Nacional de Transportes Terrestres\\CRO\\Relatorios RTA\\" + tro + ".pdf");
//            Thread.sleep(5000);

//        driver.findElement(By.id("btnSalvar")).click();


            // colar tro

            driver.switchTo().defaultContent();
            switchToFrame(driver, "ifrArvore");

            waitToBeClickableAndClickById(driver, wait, "topmenu");
            driver.findElement(By.id("topmenu")).click();
            Thread.sleep(500);

            expandTree(driver);

            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()[contains(.,'TRO - SUINF "+ tro +"')]]")));

            driver.findElement(By.xpath("//span[text()[contains(.,'TRO - SUINF "+ tro +"')]]")).click();

            driver.switchTo().defaultContent();

            switchToFrame(driver, "ifrVisualizacao");

            driver.findElement(By.cssSelector("[src='imagens/sei_editar_conteudo.gif']")).click();

            Thread.sleep(3000);


            // change window

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


//                    switchToFrame(driver, "cke_214_fileInput");
                    driver.switchTo().defaultContent();
                    driver.switchTo().frame(i);
                    WebElement fileInput = driver.findElement(By.id("cke_213_fileInput_input"));
                    fileInput.sendKeys("C:\\TROs.E.AIs\\TRO-" + tro + "-2020-COINFRS-SUINF.jpg");
                    Thread.sleep(5000);

//                driver.findElement(By.id("cke_213_fileInput_input")).click();
                }
            }

                    System.out.println("apertando botão ok");

                    Pattern OkButton = new Pattern("E:\\QA.png");
                    s.click(OkButton);


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

                    // criei uma lista de tro criados.
//                com essa lista, deve depois criar os Anexos.

                    // checar em qual pegou no bloco try abaixo.. mas funcionou !

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

//                }
//            }
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

//    private static void switchToFrame0(WebDriver driver){
//        driver.switchTo().defaultContent();
//        driver.switchTo().frame(0);
//    }

//    private static void switchToFrame1(WebDriver driver){
//        driver.switchTo().defaultContent();
//        driver.switchTo().frame(1);
//    }

    private static void closeAllPopupWindows(WebDriver driver, String mainWindow) {
        for (String windowHandle : driver.getWindowHandles()) {
            if (!mainWindow.contentEquals(windowHandle)) {
                driver.switchTo().window(windowHandle);
                driver.close();

            }

        }
        driver.switchTo().window(mainWindow);
    }

}


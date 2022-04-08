import javassist.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2021/11/9
 **/
public class CharlesCrack {
    public static void main(String[] args) {
        //This is a 30 day trial version. If you continue using Charles you must\npurchase a license. Please see the Help menu for details.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入charles安装的目录：");
        try {
            String charlesPath = br.readLine();
            String charlesJarPath = charlesPath + "\\lib\\charles.jar";
            File charlesJarFile = new File(charlesJarPath);
            if (!charlesJarFile.exists()) {
                System.out.println("charles目录(" + charlesJarPath + ")不存在!");
                return;
            }
            System.out.println("charles检测通过");
            System.out.println("正在处理文件...");
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(charlesJarPath);
            CtClass ctClass = pool.get("com.xk72.charles.p");
            CtMethod xdKP = ctClass.getMethod("a", "()Z");
            xdKP.setBody("{ return true; }");

            CtMethod uQqp = ctClass.getMethod("c", "()Ljava/lang/String;");
            uQqp.setBody("{ return \"River的证书-.-\";}");

            ctClass.writeFile();
            System.out.println("文件处理完成！");

            String exec = String.format("jar uvf %s %s", charlesJarPath, "com\\xk72\\charles\\p.class");
            Runtime.getRuntime().exec(exec);

            System.out.println("破解完成！");
        } catch (IOException | NotFoundException | CannotCompileException e) {
            e.printStackTrace();
        }
    }
}

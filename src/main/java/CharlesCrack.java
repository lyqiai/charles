import org.objectweb.asm.*;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * @Author: River
 * @Emial: 1632958163@qq.com
 * @Create: 2022/3/24
 **/
public class CharlesCrack {
    public static void main(String[] args) throws Exception {
        //This is a 30 day trial version. If you continue using Charles you must\npurchase a license. Please see the Help menu for details.
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入charles安装的目录：");

        String charlesPath = br.readLine();
        String charlesJarPath = charlesPath + "\\lib\\charles.jar";
        File charlesJarFile = new File(charlesJarPath);
        if (!charlesJarFile.exists()) {
            System.out.println("charles目录(" + charlesJarPath + ")不存在!");
            return;
        }
        System.out.println("charles检测通过");
        System.out.println("正在处理文件...");

        final int readerOption = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        ClassReader cr;
        ClassWriter cw = null;

        JarFile jarFile = new JarFile(charlesJarPath);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.equals("com/xk72/charles/p.class")) {
                ZipEntry zipEntry = new ZipEntry(entryName);
                InputStream inputStream = jarFile.getInputStream(zipEntry);
                cr = new ClassReader(inputStream);
                cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                cr.accept(new ClassVisitor(Opcodes.ASM7, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        if (mv != null) {
                            if (name.equals("c") && descriptor.equals("()Ljava/lang/String;")) {
                                mv = new MethodVisitor(Opcodes.ASM7, mv) {
                                    @Override
                                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                                    }

                                    @Override
                                    public void visitInsn(int opcode) {
                                        if (opcode == Opcodes.ARETURN) {
                                            visitInsn(Opcodes.POP);
                                            visitLdcInsn("River破解");
                                        }
                                        super.visitInsn(opcode);
                                    }
                                };
                            }

                            if (name.equals("a") && descriptor.equals("()Z")) {
                                mv = new MethodVisitor(Opcodes.ASM6, mv) {
                                    @Override
                                    public void visitInsn(int opcode) {
                                        if (opcode == Opcodes.IRETURN) {
                                            visitInsn(Opcodes.POP);
                                            visitInsn(Opcodes.ICONST_1);
                                        }
                                        super.visitInsn(opcode);
                                    }
                                };
                            }
                        }
                        return mv;
                    }
                }, readerOption);
            }
        }

        writeJarFile(charlesJarPath, "com/xk72/charles/p.class", cw.toByteArray());

        System.out.println("破解完成！");
    }


    public static void writeJarFile(String jarFilePath, String entryName, byte[] data) throws Exception {

        //1、首先将原Jar包里的所有内容读取到内存里，用TreeMap保存
        JarFile jarFile = new JarFile(jarFilePath);
        //可以保持排列的顺序,所以用TreeMap 而不用HashMap
        TreeMap tm = new TreeMap();
        Enumeration es = jarFile.entries();
        while (es.hasMoreElements()) {
            JarEntry je = (JarEntry) es.nextElement();
            byte[] b = readStream(jarFile.getInputStream(je));
            tm.put(je.getName(), b);
        }

        JarOutputStream jos = new JarOutputStream(new FileOutputStream(jarFilePath));
        Iterator it = tm.entrySet().iterator();
        boolean has = false;

        //2、将TreeMap重新写到原jar里，如果TreeMap里已经有entryName文件那么覆盖，否则在最后添加
        while (it.hasNext()) {
            Map.Entry item = (Map.Entry) it.next();
            String name = (String) item.getKey();
            JarEntry entry = new JarEntry(name);
            jos.putNextEntry(entry);
            byte[] temp;
            if (name.equals(entryName)) {
                //覆盖
                temp = data;
                has = true;
            } else {
                temp = (byte[]) item.getValue();
            }
            jos.write(temp, 0, temp.length);
        }

        if (!has) {
            //最后添加
            JarEntry newEntry = new JarEntry(entryName);
            jos.putNextEntry(newEntry);
            jos.write(data, 0, data.length);
        }
        jos.finish();
        jos.close();

    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inStream.read(buffer)) != -1) {
            outSteam.write(buffer, 0, len);
        }
        outSteam.close();
        inStream.close();
        return outSteam.toByteArray();
    }
}
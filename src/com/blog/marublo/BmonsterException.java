package com.blog.marublo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BmonsterException extends Exception {
	private static final long serialVersionUID = 1L; 
	
	//コンストラクタ
	BmonsterException(String msg) {
		super(msg);
		getShellCall();
	}
	
	public static void getShellCall() {
        BufferedReader br = null;
        // 起動するコマンド、引数でProcessBuilderを作る。
        ProcessBuilder pb = new ProcessBuilder("/root/tiritir_script/automationLessson.sh");
        // 実行するプロセスの標準エラー出力を標準出力に混ぜる。(標準エラー出力を標準入力から入力できるようになる)
        pb.redirectErrorStream(true);
        try {
            // プロセス起動
            Process process = pb.start();

            // 起動したプロセスの標準出力を取得して表示する。
            //   標準出力やエラー出力が必要なくても読んどかないとバッファがいっぱいになって
            //   プロセスが止まる(一時停止)してしまう場合がある。
            InputStream is = process.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while(true) {
                String line = br.readLine();
                if(line == null) {
                    break;
                }
                System.out.println(line);
            }
            // プロセスの終了を待つ。
            int ret = process.waitFor();
            // 終了コードを表示
            System.out.println("ret = " + ret);
        } catch (IOException ex) {
        		System.out.println(ex);
            //Logger.getLogger(TestProcess.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
           // Logger.getLogger(TestProcess.class.getName()).log(Level.SEVERE, null, ex);
        	System.out.println(ex);
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException ex) {
                    //Logger.getLogger(TestProcess.class.getName()).log(Level.SEVERE, null, ex);
                		System.out.println(ex);
                }
            }
        }
	}
	
}

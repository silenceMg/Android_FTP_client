package com.sam.remote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.commons.io.input.TailerListenerAdapter;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class activitiMain extends Activity {

	//private String sDir = "/public_html";
	//private String array_spinner[];
	ScrollView sv;
	static public String _fileName = "";
	private LinearLayout.LayoutParams LP_FF = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHanlder(this));

	}

	private void loadLogs() {
		new processTask().execute();
	}


		private class processTask extends AsyncTask<String, Void, Void> {
			private ProgressDialog Dialog = new ProgressDialog(activitiMain.this);
			private TextView log;




			protected void onPreExecute() {
				Dialog.setMessage("Loading...");
				Dialog.show();
			}

			@Override
			protected Void doInBackground(String... arg0) {
				FTPClient client = new FTPClient();

				final Spinner s = new Spinner(activitiMain.this);;
				s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

						_fileName = s.getSelectedItem().toString();
					}

					public void onNothingSelected(AdapterView<?> parent) {

					}
				});

				try {
					SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
					String ipaddr = myPref.getString("etServer", "192.168.1.102");
					String port = myPref.getString("port", "21");
					String filePath = myPref.getString("path", "D:\ftp");
					String uname = myPref.getString("uname", "1");
					String pass = myPref.getString("pass", "1");

					client.connect(ipaddr, 21);
					client.setControlEncoding("GBK");//UTF-8
					client.enterLocalPassiveMode();
					client.login(uname, pass);


					// 判断服务器返回值，验证是否已经连接上
					if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
						client.disconnect();
						System.err.println("FTP server refused connection.");
					}


					client.changeWorkingDirectory(filePath);
					System.out.println(client.printWorkingDirectory());

					sv = new ScrollView(activitiMain.this);

					sv.setLayoutParams(LP_FF);
					LinearLayout loglay = new LinearLayout(activitiMain.this);
					loglay.setOrientation(LinearLayout.VERTICAL);
					FTPFile[] ftpFiles = client.listFiles();
					ArrayList<String> name = new ArrayList<String>();


					for (int i = 0; i < ftpFiles.length; i++) {
						String fname = ftpFiles[i].getName();
						Log.i("FTP", "File " + i + " : " + fname);
						name.add(fname);
						long length = ftpFiles[i].getSize();
					}

					String[] files = name.toArray(new String[name.size()]);
					ArrayAdapter adapter2 = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, files);
					adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					s.setAdapter(adapter2);
					loglay.addView(s);


					Button btn1 = new Button(activitiMain.this);// = (Button) findViewById(R.id.button1);
					btn1.setWidth(50);
					btn1.setHeight(30);
					btn1.setText("download");
					loglay.addView(btn1);
					btn1.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            downloadFile(_fileName);
                            return true;
                        }
                    });



					sv.addView(loglay);
					client.logout();


				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						client.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}




				return null;
			}

			protected void onPostExecute(Void unused) {
				Dialog.dismiss();
				setContentView(sv);
			}




		}

	public void downloadFile(String fileName) {
		FTPClient client = new FTPClient();

			SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			String ipaddr = myPref.getString("etServer", "192.168.1.102");
			String port = myPref.getString("port", "21");
			//String logFile = myPref.getString("logfname", "error_log");
			String filePath = myPref.getString("path", "D:\ftp");
			String uname = myPref.getString("uname", "1");
			String pass = myPref.getString("pass", "1");
			try {
			client.connect(ipaddr, 21);
			client.setControlEncoding("GBK");//UTF-8
			client.enterLocalPassiveMode();
			client.login(uname, pass);


			// 判断服务器返回值，验证是否已经连接上
			if (!FTPReply.isPositiveCompletion(client.getReplyCode())) {
				client.disconnect();
				System.err.println("FTP server refused connection.");
			}
		}
			catch (IOException e) {
				e.printStackTrace();
			}


		try {
			client.changeWorkingDirectory(filePath);

		client.enterLocalPassiveMode();
		// 设置以二进制方式传输

			client.setFileType(FTP.BINARY_FILE_TYPE);
		} catch (IOException e) {
			//logger.error("设置以二进制传输模式失败...", e);
		}
		InputStream in = null;

		OutputStream out = null;
        File file = new File(android.os.Environment.getExternalStorageDirectory().toString()+"/"+_fileName);
		byte[] b = new byte[1024];
		try {
			in = client.retrieveFileStream(new String(fileName.getBytes("UTF-8"), "iso8859-1"));
             out = new FileOutputStream(file,true);
        }
		catch (IOException ex)
		{}
       // OutputStreamWriter outWriter = new OutputStreamWriter (out);

		while (true) {
			try {
				int num = in.read(b);
				if (num == -1)
					break;
                out.write(b, 0, num);
			} catch (IOException ex) {
				//Do something witht the exception
			}
		}
		try {
            out.flush();
			in.close();
			out.close();
		} catch (IOException ex) {
			//Do something witht the exception
		}
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	public class PCTailListener extends TailerListenerAdapter {
			public void handle(String line) {
				System.out.println(line);
			}
		}
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.mainmenu, menu);
			return true;
		}
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case R.id.refresh:
				loadLogs();
				return true;
			case R.id.settings:
				Intent settingsActivity = new Intent(this, Preferences.class);
				startActivityForResult(settingsActivity,0);
			default:
				return super.onOptionsItemSelected(item);
			}
		}
	}

using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.IO.Ports;
using SimpleWifi.Win32;
using SimpleWifi.Win32.Interop;

namespace Smart_Garage_Configurator
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        string[] prevPorts;
        string[] ports;
        string wifiSSID = "";
        string wifiPassword = "";
        int numberOfTick = 0;
        List<string> networks;
        List<string> prev_networks;
        SerialPort device;
        WlanClient client;

        private void button4_Click(object sender, EventArgs e)
        {
            if (textBox2.PasswordChar.Equals('●')) textBox2.PasswordChar = '\0';
            else textBox2.PasswordChar = '●';
            textBox2.Focus();
        }

        private void Form1_Load(object sender, EventArgs e)
        {
            prevPorts = SerialPort.GetPortNames();
            timer1.Start();
            client = new WlanClient();
            prev_networks = new List<string>();
            networks = new List<string>();
            timer2.Start();
        }

        private void ListPorts(bool prevOrCurrent)
        {
            listBox1.Items.Clear();
            if (prevOrCurrent) foreach (string p in prevPorts) listBox1.Items.Add(p);
            else foreach (string p in ports) listBox1.Items.Add(p);
        }

        private void ConsoleWrite(string text)
        {
            if (richTextBox1.Text == "") richTextBox1.Text += DateTime.Now.Hour.ToString() + ":" + DateTime.Now.Minute.ToString() + ":" +
                    DateTime.Now.Second.ToString() + "> " + text;
            else richTextBox1.Text += "\n" + DateTime.Now.Hour.ToString() + ":" + DateTime.Now.Minute.ToString() + ":" +
                    DateTime.Now.Second.ToString() + "> " + text;
        }

        public void FillListBox()
        {
            listBox2.Items.Clear();
            if (networks.Count > 0) foreach (string network in networks) listBox2.Items.Add(network);
            else listBox2.Items.Add("Ağ Bulunamadı");
        }

        public void WiFi_Get()
        {
            if (WiFi_NumberofAdapters() > 0 && WiFi_Control())
            {
                networks = WiFi_Scan();
                networks = WiFi_SortList(networks);
            }
        }

        public List<string> WiFi_SortList(List<string> names)
        {
            for (int i = 0; i < names.Count; i++)
            {
                for (int j = i + 1; j < names.Count; j++) if (names[j].Equals(names[i])) names.RemoveAt(j);
                if (names[i].Trim().Equals("")) names.RemoveAt(i);
            }
            names.Sort();
            return names;
        }

        public int WiFi_NumberofAdapters()
        {
            return client.Interfaces.Length;
        }

        public bool WiFi_Control()
        {
            bool on = true;
            foreach (WlanInterface interf in client.Interfaces)
            {
                try
                {
                    interf.GetAvailableNetworkList(0);
                }
                catch (Win32Exception er)
                {
                    on = false;
                }
            }
            return on;
        }

        public List<string> WiFi_Scan()
        {
            List<string> names = new List<string>();
            foreach (WlanInterface interf in client.Interfaces) foreach (WlanAvailableNetwork network in interf.GetAvailableNetworkList(0))
                {
                    names.Add(Encoding.ASCII.GetString(network.dot11Ssid.SSID, 0, (int)network.dot11Ssid.SSIDLength));
                }
            return names;
        }

        private void timer1_Tick(object sender, EventArgs e)
        {
            if (prevPorts.Length > 0 && ports == null) ListPorts(true);
            ports = SerialPort.GetPortNames();
            if (ports.Length != prevPorts.Length) ListPorts(false);
            prevPorts = ports;
        }

        private void button1_Click(object sender, EventArgs e)
        {
            if (button1.Text == "Bağla")
            {
                if (listBox1.SelectedIndex == -1) ConsoleWrite("Aygıt Seçilmedi");
                else
                {
                    bool err = false;
                    device = new SerialPort(listBox1.Items[listBox1.SelectedIndex].ToString(), 9600);
                    try
                    {
                        device.Open();
                    }
                    catch
                    {
                        err = true;
                        ConsoleWrite("Aygıt Bağlantısı Başarısız");
                    }
                    if (!err && device.IsOpen)
                    {
                        ConsoleWrite("Aygıt Bağlantısı Kuruldu");
                        timer1.Stop();
                        button1.Text = "Bağlantıyı Kes";
                        label3.Text = "Aygıt: " + listBox1.Items[listBox1.SelectedIndex].ToString();
                    }
                }
            } else
            {
                bool err = false;
                try
                {
                    device.Close();
                }
                catch
                {
                    err = true;
                    ConsoleWrite("Aygıt Bağlantısı Kesilmiş");
                }
                if (!err && !device.IsOpen)
                {
                    ConsoleWrite("Aygıt Bağlantısı Kesildi");
                    timer1.Start();
                    button1.Text = "Bağla";
                    label3.Text = "Aygıt:";
                    device = null;
                }
            }          
        }

        private void richTextBox1_TextChanged(object sender, EventArgs e)
        {
            richTextBox1.SelectionStart = richTextBox1.Text.Length;
            richTextBox1.ScrollToCaret();
        }

        private void timer2_Tick(object sender, EventArgs e)
        {
            WiFi_Get();
            if (networks.Count != prev_networks.Count) FillListBox();
            prev_networks = networks;
        }

        private void listBox2_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (listBox2.SelectedIndex != -1)
            if (listBox2.Items[listBox2.SelectedIndex].ToString() != "Ağ Bulunamadı") textBox1.Text = listBox2.Items[listBox2.SelectedIndex].ToString();
        }

        private string PasswordReturn()
        {
            string pass = "";
            if (textBox2.Text.Length == 1) pass = "●";
            if (textBox2.Text.Length == 2) pass = "●●";
            if (textBox2.Text.Length > 2) for (int i = 0; i < textBox2.Text.Length; i++) if (i < 2) pass += textBox2.Text[i]; else pass += "●";
            return pass;
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (textBox1.Text == "" || textBox2.Text == "") ConsoleWrite("Ağ Bilgileri Boş Bırakılmış");
            else
            {
                label4.Text = "Ağ Adı: " + textBox1.Text;
                label5.Text = "Ağ Şifresi: " + PasswordReturn();
                wifiSSID = textBox1.Text;
                wifiPassword = textBox2.Text;
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            if (device == null) ConsoleWrite("Aygıt Bağlantısı Yok");
            else if (!device.IsOpen) ConsoleWrite("Aygıt Bağlantısı Yok");
            else
            {
                if (wifiSSID == "" || wifiPassword == "") ConsoleWrite("Ağ Bilgileri Eksik");
                else
                {
                    device.Write(wifiSSID + ":" + wifiPassword);
                    ConsoleWrite("Bilgiler Gönderildi Cevap Bekleniyor");
                    button3.Enabled = false;
                    timer3.Start();
                }
            }
        }

        private void timer3_Tick(object sender, EventArgs e)
        {
            if (device.IsOpen)
            {
                string receiveText = device.ReadExisting();
                if (receiveText != null)
                    if (receiveText.Equals("a"))
                    {
                        ConsoleWrite("İşlem Başarıyla Bitti");
                        button3.Enabled = true;
                        timer3.Stop();
                    }
                numberOfTick += 1;
                if (numberOfTick > 150)
                {
                    ConsoleWrite("İşlemleri Tekrar Deneyin");
                    button3.Enabled = true;
                    numberOfTick = 0;
                    timer3.Stop();
                }
            }            
        }
    }
}

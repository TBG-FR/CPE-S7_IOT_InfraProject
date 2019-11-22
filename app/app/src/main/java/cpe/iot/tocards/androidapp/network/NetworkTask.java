//package cpe.iot.tocards.androidapp;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//public class NetworkTask extends AsyncTask<String, Void, DatagramSocket> {
//
//    MainActivity App;
//
//    NetworkTask(MainActivity app) {
//        App = app;
//    }
//
//    @Override
//    protected DatagramSocket doInBackground(String... strings) {
//
//        DatagramSocket Socket = null;
//        InetAddress dstAddress = null;
//        int dstPort = -1;
//
//        try
//        {
//            if(!(strings[0] == null || strings[0].isEmpty()))
//            {
//                dstAddress = InetAddress.getByName(strings[0]);
//            }
//            if(!(strings[1] == null || strings[1].isEmpty())) {
//                dstPort =  Integer.parseInt(strings[1]);
//            }
//            if(dstAddress != null && dstPort != -1)
//            {
//                Socket = new DatagramSocket(dstPort);
//                Socket.connect(dstAddress, dstPort);
//                Log.d("Socket.Connect", "ADDR=" + dstAddress + " -- PORT=" + dstPort + " -> CONN=" + Socket.isConnected());
//            }
//        }
//        catch(SocketException | UnknownHostException ex)
//        {
//            Log.e("Socket.Connect", "Err " + ex.getMessage());
//
//            //Socket.disconnect();
//            //Socket.close();
//            //Socket = null;
//
//            ex.printStackTrace();
//        }
////        finally
////        {
////        }
//
//        return Socket;
//    }
//
//    @Override
//    protected void onPostExecute(DatagramSocket datagramSocket) {
//        super.onPostExecute(datagramSocket);
//
////        App.setUDPSocket(datagramSocket);
////        App.displayConnected();
//    }
//}

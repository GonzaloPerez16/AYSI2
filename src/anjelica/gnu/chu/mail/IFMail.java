
package gnu.chu.mail;

import gnu.chu.Menu.Principal;
import gnu.chu.anjelica.facturacion.lisfactu;
import gnu.chu.anjelica.ventas.lialbven;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.ventana;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * <p>Titulo: IFMail </p>
 * <p>Descripción: Ventana para mandar por Email los Albaranes y facturas</p>
 * <p>Copyright: Copyright (c) 2005-2015
 *  Este programa es software libre. Puede redistribuirlo y/o modificarlo bajo
 *  los terminos de la Licencia Pública General de GNU según es publicada por
 *  la Free Software Foundation, bien de la versión 2 de dicha Licencia
 *  o bien (según su elección) de cualquier versión posterior.
 *  Este programa se distribuye con la esperanza de que sea útil,ed
 *  pero SIN NINGUNA GARANTIA, incluso sin la garantía MERCANTIL implícita
 *  o sin garantizar la CONVENIENCIA PARA UN PROPOSITO PARTICULAR.
 *  Véase la Licencia Pública General de GNU para más detalles.
 *  Debería haber recibido una copia de la Licencia Pública General junto con este programa.
 *  Si no ha sido así, escriba a la Free Software Foundation, Inc.,
 *  en 675 Mass Ave, Cambridge, MA 02139, EEUU.
 * </p>
 * @author chuchiP
 * @version 1.0 Inicial
 */
public class IFMail extends ventana {
    private ventana padre;
    lialbven liAlb = null;
    lisfactu liFra = null;
    String tipoDoc;
//    String docSerie;
//    int empCodi,docAno,docNume;
    boolean opValora;
    String sqlDoc;


    public IFMail() {
        initComponents();
        setSize(new Dimension(500,350));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        cTextArea1 = new gnu.chu.controles.CTextArea();
        cli_codiE = new gnu.chu.camposdb.cliPanel();
        cLabel1 = new gnu.chu.controles.CLabel();
        cLabel2 = new gnu.chu.controles.CLabel();
        cLabel3 = new gnu.chu.controles.CLabel();
        scmsgE = new javax.swing.JScrollPane();
        respuestE = new gnu.chu.controles.CTextArea();
        Baceptar = new gnu.chu.controles.CButton(Iconos.getImageIcon("check"));
        Bcancelar = new gnu.chu.controles.CButton(Iconos.getImageIcon("cancel"));
        cLabel4 = new gnu.chu.controles.CLabel();
        asuntoE = new gnu.chu.controles.CTextField(Types.CHAR,"X",100);
        cli_emailE = new gnu.chu.controles.CLinkBox();
        opCopia = new gnu.chu.controles.CCheckBox();
        cLabel5 = new gnu.chu.controles.CLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        toEmailE = new gnu.chu.controles.CTextArea();
        BIncluir = new gnu.chu.controles.CButton();

        cTextArea1.setColumns(20);
        cTextArea1.setRows(5);
        jScrollPane1.setViewportView(cTextArea1);

        setTitle("Enviar Correo Electronico");
        getContentPane().setLayout(null);
        getContentPane().add(cli_codiE);
        cli_codiE.setBounds(53, 11, 421, 18);

        cLabel1.setText("Cliente");
        getContentPane().add(cLabel1);
        cLabel1.setBounds(10, 14, 39, 15);

        cLabel2.setText("Correo");
        getContentPane().add(cLabel2);
        cLabel2.setBounds(10, 35, 39, 15);

        cLabel3.setBackground(java.awt.Color.blue);
        cLabel3.setForeground(new java.awt.Color(255, 255, 255));
        cLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        cLabel3.setText("Introduzca un texto a acompañar al Correo");
        cLabel3.setOpaque(true);
        getContentPane().add(cLabel3);
        cLabel3.setBounds(10, 130, 466, 15);

        scmsgE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        respuestE.setBorder(null);
        respuestE.setColumns(20);
        respuestE.setRows(5);
        scmsgE.setViewportView(respuestE);

        getContentPane().add(scmsgE);
        scmsgE.setBounds(10, 148, 466, 90);

        Baceptar.setText("Aceptar");
        getContentPane().add(Baceptar);
        Baceptar.setBounds(140, 244, 156, 32);

        Bcancelar.setText("Cancelar");
        getContentPane().add(Bcancelar);
        Bcancelar.setBounds(304, 244, 156, 32);

        cLabel4.setText("Asunto");
        getContentPane().add(cLabel4);
        cLabel4.setBounds(10, 110, 40, 15);
        getContentPane().add(asuntoE);
        asuntoE.setBounds(60, 110, 410, 17);

        cli_emailE.setAncTexto(180);
        getContentPane().add(cli_emailE);
        cli_emailE.setBounds(53, 35, 330, 17);

        opCopia.setSelected(true);
        opCopia.setText("Copia Local");
        getContentPane().add(opCopia);
        opCopia.setBounds(20, 244, 90, 23);

        cLabel5.setText("A");
        getContentPane().add(cLabel5);
        cLabel5.setBounds(10, 57, 20, 15);

        toEmailE.setColumns(20);
        toEmailE.setRows(5);
        jScrollPane2.setViewportView(toEmailE);

        getContentPane().add(jScrollPane2);
        jScrollPane2.setBounds(30, 60, 440, 40);

        BIncluir.setText("Incluir");
        getContentPane().add(BIncluir);
        BIncluir.setBounds(390, 35, 70, 19);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void iniciar(ventana padre) throws SQLException
    {
        this.padre=padre;
        dtStat=padre.dtStat;
        this.EU=padre.EU;
        this.ct=padre.ct;
        cli_codiE.iniciar(dtStat, this, padre.vl, EU);
        activarEventos();
    }
    public void setLialbven(lialbven liAlb)
    {
       this.liAlb=liAlb;
    }
    public void setLisfactu (lisfactu liFra)
    {
        this.liFra=liFra;
    }
    public void setCliCodi(String cliCodi)
    {
        if (!cli_codiE.getText().equals(cliCodi))
        {
            cli_codiE.setText(cliCodi);
            cli_emailE.removeAllItems();
            toEmailE.setText("");
            llenaCorreos();
            cli_emailE.resetTexto();
        }
    }
    /**
     * Llena combo de correos con los correos del cliente.
     */
    void llenaCorreos()
    {
        try
        {
            addCorreo(cli_codiE.getLikeCliente().getString("cli_email2"));
            addCorreo(cli_codiE.getLikeCliente().getString("cli_email1"));
        } catch (SQLException ex)
        {
            Logger.getLogger(IFMail.class.getName()).log(Level.SEVERE,
                "Error al llenar Correos de cliente "+cli_codiE.getText(), ex);
        }
    }
    void addCorreo(String email)
    {
        if (email==null)
            return;
        if (email.indexOf("@")>0)
            cli_emailE.addDatos(email, email);
            
    }
    public gnu.chu.camposdb.cliPanel getCliField()
    {
        return cli_codiE;
    }
   
    
    public void setDatosDoc( String tipoDoc,
         String sqlDoc,
         boolean opValora)
    {
         this.tipoDoc=tipoDoc;
         this.sqlDoc=sqlDoc;
         this.opValora=opValora;
    }
    private void activarEventos() {
        Baceptar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarEmail();
            }
        });
        Bcancelar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                padre.mensajeErr("Email ... CANCELADO");
                cancelEmail();
            }
        });
        
        BIncluir.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cli_emailE.isNull())
                    return;
                toEmailE.setText(toEmailE.getText()+
                    (toEmailE.getText().trim().equals("")?"":", ")+cli_emailE.getText());
            }
        });
    }
    void enviarEmail()
    {
      if (toEmailE.getText().trim().equals(""))
        toEmailE.setText(cli_emailE.getText());
      
      if (toEmailE.getText().trim().equals("") )
      {
      
        msgBox("Introduzca algún Correo Electronico de destino");
        return;
      }
      if (tipoDoc.equals("A"))
            enviarEmailAlb();
      else
            enviarEmailFra();
    }
    void enviarEmailFra()
    {
       try {
            liFra.setSubject(asuntoE.getText());
            liFra.setEmailCC(opCopia.isSelected()? EU.email:null);
            liFra.setAsunto(respuestE.getText());
            liFra.sendFraEmail(sqlDoc, toEmailE.getText(), cli_codiE.getValorInt() );
            HashMap hm=new HashMap();
            hm.put("%c", cli_codiE.getValorInt());
            hm.put("%a",asuntoE.getText());
            Principal.guardaMens(padre.dtCon1, "EF",hm,null,EU.usuario);
            cancelEmail();
            padre.mensajeErr("Email ..... Enviado");
        }
        catch (Exception k)
        {
          Error("Error al enviar Albaran por Email por usuario: "+EU.usuario, k);
        }
    }
    public void setAsunto(String subject)
    {
        asuntoE.setText(subject);
    }
    public void setText(String subject)
    {
        respuestE.setText(subject);
    }    void enviarEmailAlb()
    {
        try
        {
            liAlb.setSubject(asuntoE.getText());
            liAlb.setEmailCC(opCopia.isSelected()? EU.email:null);
            liAlb.envAlbaranEmail(ct.getConnection(), dtStat, sqlDoc, EU,
                            opValora,toEmailE.getText(),
                            respuestE.getText());
            HashMap hm=new HashMap();
            hm.put("%c", cli_codiE.getValorInt());
            hm.put("%a",asuntoE.getText());
            Principal.guardaMens(padre.dtCon1, "EA",hm,null,EU.usuario);
            cancelEmail();
            padre.mensajeErr("Email ..... Enviado");
        }
        catch (Exception k)
        {
           Error("Error al enviar Albaran por Email por usuario: "+EU.usuario, k);
        }
  }

  void cancelEmail()
  {
    padre.setEnabled(true);
    this.setVisible(false);
    try
    {
      this.setSelected(true);
    } catch (Exception k) { }
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gnu.chu.controles.CButton BIncluir;
    private gnu.chu.controles.CButton Baceptar;
    private gnu.chu.controles.CButton Bcancelar;
    private gnu.chu.controles.CTextField asuntoE;
    private gnu.chu.controles.CLabel cLabel1;
    private gnu.chu.controles.CLabel cLabel2;
    private gnu.chu.controles.CLabel cLabel3;
    private gnu.chu.controles.CLabel cLabel4;
    private gnu.chu.controles.CLabel cLabel5;
    private gnu.chu.controles.CTextArea cTextArea1;
    private gnu.chu.camposdb.cliPanel cli_codiE;
    private gnu.chu.controles.CLinkBox cli_emailE;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private gnu.chu.controles.CCheckBox opCopia;
    private gnu.chu.controles.CTextArea respuestE;
    private javax.swing.JScrollPane scmsgE;
    private gnu.chu.controles.CTextArea toEmailE;
    // End of variables declaration//GEN-END:variables

}

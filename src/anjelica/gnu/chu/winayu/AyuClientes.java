/**
 *
 * Author:       Chuchi P.
 *
 * Description:  Pantalla de Ayuda de Clientes,    permite Consultar clientes
 * por nombre, NIF o Razon Social.
 * Siempre sera Llamado desde otro programa.
 * <p>Copyright: Copyright (c) 2005-2016
 *   Este programa es software libre. Puede redistribuirlo y/o modificarlo bajo
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
 * <p>Empresa: MISL</p>
 * @author chuchiP
 * @version 1.0
 */
package gnu.chu.winayu;

import gnu.chu.anjelica.pad.pdconfig;
import gnu.chu.controles.StatusBar;
import gnu.chu.sql.DatosTabla;
import gnu.chu.utilidades.EntornoUsuario;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.ventana;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JLayeredPane;

public class AyuClientes extends ventana
{
  String zona=null;
  public boolean Error=false;
  public String cli_codiE="";
  public String cli_cuenE="";
  public boolean consulta=false;
  
  public AyuClientes(EntornoUsuario e,JLayeredPane fr)
  {
    this(e,fr,null);
  }
  public AyuClientes(EntornoUsuario e,JLayeredPane fr,DatosTabla dt)
  {
    setTitulo("Ayuda Clientes");
    EU = e;
    eje = false;
    if (dt!=null)
      dtCon1=dt;
    try
    {
      jbInit();
    }
    catch (Exception k)
    {
      ErrorInit(k);
    }
  }
  
   private void jbInit() throws Exception
  {
    statusBar = new StatusBar(this);
    iniciarFrame();
    
    this.setIconifiable(false);
  //  this.setResizable(false);
    this.setMaximizable(false);
    if (dtCon1 == null)
      conecta();
    
    initComponents();
    this.getContentPane().add(statusBar, BorderLayout.SOUTH);
    this.setSize(new Dimension(540, 351));
    pdconfig.llenaDiscr(dtCon1, zon_codiE, "Cz",EU.em_cod);
    Pcond.setDefButton(Baceptar);
    activarEventos();
  }
   @Override
  public void iniciarVentana() throws Exception
  {
     jt.setEnabled(true);
     cli_nombE.requestFocusLater();
     statusBar.setEnabled(true);
  }

  void activarEventos()
  {
    Baceptar.addActionListener(new java.awt.event.ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Bbuscar_actionPerformed();
      }
    });
    Bselecion.addActionListener(new java.awt.event.ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Baceptar_actionPerformed();
      }
    });
    jt.tableView.addMouseListener(new MouseAdapter()
    {
      @Override
      public void mouseClicked(MouseEvent m)
      {
        if (m.getClickCount() > 1 && jt.isVacio()==false)
        {
          Baceptar_actionPerformed();
        }
      }
    });

    jt.tableView.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if ((e.getKeyCode() == KeyEvent.VK_INSERT || e.getKeyCode() == KeyEvent.VK_ENTER) && jt.isVacio()==false)
          Baceptar_actionPerformed();
      }

    });
  }
  public void Bbuscar_actionPerformed() {
    String s,strSql;
  
    s="1=1 ";
    if (! cli_nombE.isNull())
      s+="and ( UPPER(cli_nomb) LIKE '%"+cli_nombE.getText()+"%'"+
          " or UPPER(cli_nomco) LIKE '%"+cli_nombE.getText()+"%')";

   
    if (! cli_poblE.isNull())
      s+="and  UPPER(cli_pobl) LIKE '%"+cli_poblE.getText()+"%'";


    if (! cli_activE.getValor().equals("*"))
      s+="and  cli_activ= '"+cli_activE.getValor()+"'";
    
    if (! zon_codiE.isNull())
        s+=(s.equals("")?"":"and ")+" zon_codi= '"+zon_codiE.getText()+"'";
    try
    {
        strSql = "select cli_codi,cli_nomb,cli_nomco,cli_pobl " +
            " FROM clientes " +
            " WHERE " + s +
            (zona==null?"":" AND cli_zonrep LIKE '"+zona+"'")+
            " order by cli_nomb";
//        debug("AyuCli: "+strSql);
        if (! dtCon1.select(strSql))
        {
          msgBox("NO encontrados CLIENTES con estos criterios");
          cli_nombE.requestFocus();
          return;
        }
        
        jt.setDatos(dtCon1);

      } catch (Exception k)
      {
        Error("Error al buscar clientes",k);
        return;
      }
      jt.requestFocusInicio();
      mensajeErr("Consulta de clientes, realizada");
  }

  void Baceptar_actionPerformed()
  {
    if (jt.tableView.getSelectedRow() < 0)
    {
      mensaje("SELECCIONE UNA COLUMNA EN EL GRID");
      jt.requestFocus();
      return;
    }

    // Editar Columna Activa.
    cli_codiE = jt.getValString(0);

   
    consulta = true;
    matar();
  }
  public void setZona(String zona)
  {
    this.zona=zona;
  }
  public void setCliNomb(String cliNomb)
  {
     cli_nombE.setText(cliNomb);
  }
  public String getCliNomb()
  {
     return cli_nombE.getText();
  }
  public String getNombCom()
  {
     return jt.getValString(2);
  }
  void Bcancelar_actionPerformed(ActionEvent e) {
    matar();
  }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        Pprinc = new gnu.chu.controles.CPanel();
        jt = new gnu.chu.controles.Cgrid(4);
        Pcond = new gnu.chu.controles.CPanel();
        cli_nombL = new gnu.chu.controles.CLabel();
        cli_nombE = new gnu.chu.controles.CTextField();
        cli_poblL = new gnu.chu.controles.CLabel();
        cli_poblE = new gnu.chu.controles.CTextField();
        cLabel18 = new gnu.chu.controles.CLabel();
        zon_codiE = new gnu.chu.controles.CLinkBox();
        cli_activE = new gnu.chu.controles.CComboBox();
        Baceptar = new gnu.chu.controles.CButton(Iconos.getImageIcon("check"));
        Bselecion = new gnu.chu.controles.CButton(Iconos.getImageIcon("choose"));

        Pprinc.setLayout(new java.awt.GridBagLayout());

        jt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jt.setMaximumSize(new java.awt.Dimension(239, 239));
        jt.setMinimumSize(new java.awt.Dimension(239, 239));
        jt.setName(""); // NOI18N
        jt.setBuscarVisible(false);
        ArrayList cabecera=new ArrayList();
        cabecera.add("Codigo"); // 0
        cabecera.add("Nombre Fiscal"); //1
        cabecera.add("Nombre Social"); // 2
        cabecera.add("Poblaccion"); // 3
        jt.setCabecera(cabecera);
        int i []= {50,300,300,300};
        jt.setAnchoColumna(i);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        Pprinc.add(jt, gridBagConstraints);

        Pcond.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Pcond.setLayout(null);

        cli_nombL.setText("Nombre");
        Pcond.add(cli_nombL);
        cli_nombL.setBounds(2, 2, 50, 15);

        cli_nombE.setMayusc(true);
        Pcond.add(cli_nombE);
        cli_nombE.setBounds(50, 2, 270, 17);

        cli_poblL.setText("Poblac.");
        Pcond.add(cli_poblL);
        cli_poblL.setBounds(2, 22, 50, 15);

        cli_poblE.setMayusc(true);
        Pcond.add(cli_poblE);
        cli_poblE.setBounds(50, 22, 270, 17);

        cLabel18.setText("Zona");
        cLabel18.setPreferredSize(new java.awt.Dimension(60, 18));
        Pcond.add(cLabel18);
        cLabel18.setBounds(10, 40, 40, 18);

        zon_codiE.setAncTexto(30);
        zon_codiE.setMayusculas(true);
        zon_codiE.setPreferredSize(new java.awt.Dimension(152, 18));
        Pcond.add(zon_codiE);
        zon_codiE.setBounds(50, 40, 200, 18);

        cli_activE.addItem("Activo", "S");
        cli_activE.addItem("Inact.", "N");
        cli_activE.addItem("Todos", "*");
        Pcond.add(cli_activE);
        cli_activE.setBounds(260, 40, 60, 20);

        Baceptar.setText("Buscar");
        Pcond.add(Baceptar);
        Baceptar.setBounds(330, 0, 90, 30);

        Bselecion.setText("Selecionar");
        Pcond.add(Bselecion);
        Bselecion.setBounds(330, 40, 90, 24);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 426;
        gridBagConstraints.ipady = 76;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        Pprinc.add(Pcond, gridBagConstraints);

        getContentPane().add(Pprinc, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gnu.chu.controles.CButton Baceptar;
    private gnu.chu.controles.CButton Bselecion;
    private gnu.chu.controles.CPanel Pcond;
    private gnu.chu.controles.CPanel Pprinc;
    private gnu.chu.controles.CLabel cLabel18;
    private gnu.chu.controles.CComboBox cli_activE;
    private gnu.chu.controles.CTextField cli_nombE;
    private gnu.chu.controles.CLabel cli_nombL;
    private gnu.chu.controles.CTextField cli_poblE;
    private gnu.chu.controles.CLabel cli_poblL;
    private gnu.chu.controles.Cgrid jt;
    private gnu.chu.controles.CLinkBox zon_codiE;
    // End of variables declaration//GEN-END:variables
}

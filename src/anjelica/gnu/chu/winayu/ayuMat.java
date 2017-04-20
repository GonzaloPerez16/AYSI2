package gnu.chu.winayu;


import java.awt.*;
import java.awt.event.*;
import gnu.chu.controles.*;
import gnu.chu.utilidades.*;
import gnu.chu.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * <p>Título: ayumat</p>
 * <p>Descripcion: Pantalla de Ayuda de Mataderos</p>
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
 * <p>Empresa: MISL</p>
 * @author chuchiP
 * @version 1.0
 */

public class ayuMat extends ventana implements  Runnable
{    
  BorderLayout bLay1 = new BorderLayout();
  String strSql="";
  public String mat_codiT="";
  public String mat_nombT="";
  public boolean consulta=false;
  CPanel Pprinc = new CPanel();
  Cgrid jt = new Cgrid(3);
  CPanel Pcons = new CPanel();
  CLabel mat_nombL = new CLabel();
  CTextField mat_nombE = new CTextField(java.sql.Types.CHAR,"X",50);
  CButton Belegir = new CButton(Iconos.getImageIcon("rotarup"));
  CButton Baceptar = new CButton(Iconos.getImageIcon("buscar"));
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  CLabel cLabel1 = new CLabel();
  CTextField mat_nrgsaE = new CTextField(java.sql.Types.CHAR,"X",12);

  public ayuMat(EntornoUsuario e,JLayeredPane fr)
  {
    this(e,fr,null);
  }

  /**
  *  Recibe el Entorno Usuario, el Principal
  */
 public ayuMat(EntornoUsuario e, JLayeredPane fr, DatosTabla dt)
 {
   setTitulo("Ayuda Mataderos");
   eje = true;

   EU = e;
//    jf=fr;
   if (dt != null)
     dtCon1 = dt;

   try
   {
     jbInit();
   }
   catch (Exception k)
   {
     setErrorInit(true);
   }
 }

  private void jbInit() throws Exception
  {
    statusBar = new StatusBar(this);
    this.setTitle(getTitulo());
    this.setResizable(true);

    this.setIconifiable(false);
    this.setSize(new Dimension(487, 482));

    if (dtCon1==null)
      conecta();


    Pcons.setMaximumSize(new Dimension(455, 41));
    Pcons.setMinimumSize(new Dimension(455, 41));
    Pcons.setPreferredSize(new Dimension(455, 41));
    Pcons.setDefButton(Baceptar);
    mat_nombL.setMaximumSize(new Dimension(47, 16));
    mat_nombL.setMinimumSize(new Dimension(47, 16));
    mat_nombL.setPreferredSize(new Dimension(47, 16));
    mat_nombL.setText("Nombre");
    mat_nombL.setBounds(new Rectangle(1, 3, 47, 16));

    Baceptar.setText("F4 Buscar");
    Baceptar.setBounds(new Rectangle(312, 3, 133, 28));
    Baceptar.setMaximumSize(new Dimension(133, 28));
    Baceptar.setMinimumSize(new Dimension(133, 28));
    Baceptar.setPreferredSize(new Dimension(133, 28));
    Baceptar.setMargin(new Insets(0, 0, 0, 0));
    Baceptar.setMnemonic('B');

    Pcons.setLayout(null);
    Pprinc.setLayout(gridBagLayout2);

    // Configurando el Grid.
    ArrayList cabecera = new ArrayList();
    cabecera.add("Codigo"); // 0 -- Codigo
    cabecera.add("Nombre"); //1 -- Nombre
    cabecera.add("N.Reg.San."); // 2 -- Num.Reg.San
    jt.setCabecera(cabecera);
    int i []= {46,283,283};
    jt.setAnchoColumna(i);
    int a[]= {1,0,0};
    jt.setAlinearColumna(a);
    jt.setAjustarColumnas(true);
    jt.setNumRegCargar(100);
    jt.setAjustarGrid(true);
//    jt.setIgnoraColumna(0,true);
    jt.setBackground(Color.orange);
    jt.setMaximumSize(new Dimension(455, 265));
    jt.setMinimumSize(new Dimension(455, 265));
    jt.setOpaque(true);
    jt.setPreferredSize(new Dimension(455, 265));
    jt.setNumRegCargar(25);

    Belegir.setEnabled(false);
    Pprinc.setDefButton(Belegir);
    Belegir.setMargin(new Insets(0, 0, 0, 0));
    Belegir.setToolTipText("Elegir el Registro Activo");

        // Poniendo Orejas.
    Belegir.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Belegir_actionPerformed();
      }
    });
    Baceptar.addActionListener(new java.awt.event.ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Baceptar_actionPerformed();
      }
    });

    jt.tableView.addMouseListener(new MouseAdapter()
    {
            @Override
      public void mouseClicked(MouseEvent m)
      {
        if (m.getClickCount() > 1)
        {
          Belegir_actionPerformed();
        }
      }
    });
    jt.tableView.addKeyListener(new KeyAdapter()
    {
            @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.getKeyCode() == KeyEvent.VK_INSERT || e.getKeyCode() == KeyEvent.VK_ENTER)
          Belegir_actionPerformed();
      }
    });
    Belegir.setToolTipText("Elegir el Registro Activo");

    mat_nombE.setBounds(new Rectangle(48, 3, 259, 15));

    cLabel1.setText("Nº Reg. Sanitario");
    cLabel1.setBounds(new Rectangle(26, 20, 99, 16));
    mat_nrgsaE.setBounds(new Rectangle(129, 20, 175, 15));
    Belegir.setBounds(200,0,25,22);
    this.getContentPane().add(statusBar, BorderLayout.SOUTH);
    this.getContentPane().add(Pprinc, BorderLayout.CENTER);

    Pprinc.add(jt,  new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 1, 0, 5), 0, 0));
    Pprinc.add(Pcons,     new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 1, 5, 5), 0, 0));
    Pcons.add(mat_nombE, null);
    Pcons.add(mat_nombL, null);
    Pcons.add(Baceptar, null);
    Pcons.add(mat_nrgsaE, null);
    Pcons.add(cLabel1, null);

    jt.panelBuscar.add(Belegir,null);

  }
    @Override
  public void iniciarVentana() throws Exception
  {
     Pcons.setDefButton(Baceptar);
     Pcons.setButton(KeyEvent.VK_F4, Baceptar);

     mat_nombE.setColumnaAlias("mat_nomb");
     mat_nrgsaE.setColumnaAlias("mat_nrgsa");
     mat_nombE.setQuery(true);
     mat_nrgsaE.setQuery(true);
     mat_nombE.requestFocusLater();
  }
  void Baceptar_actionPerformed()
  {
    String s="";
    ArrayList v=new ArrayList();
    v.add(mat_nombE.getStrQuery());
    v.add(mat_nrgsaE.getStrQuery());

    strSql="select mat_codi,mat_nomb,mat_nrgsa FROM v_matadero ";
    strSql = creaWhere(strSql, v,true);
    strSql+=" order by mat_codi";
    Thread th= new Thread(this);
    th.setPriority(Thread.MAX_PRIORITY);
    th.start();
  }

  public boolean rgSelect()
  {
    mensaje("Buscando Registros ....");
    Belegir.setEnabled(false);
    try
    {
      if (dtCon1.select(strSql, false) == false)
      {
        mensaje(" -- NO ENCONTRADOS Mataderos con esas Condiciones -- ");
        return true;
      }
    }
    catch (SQLException k)
    {
      Error("Error al Buscar Mataderos: ", dtCon1.SqlException);
      return false;
    }
    //	Llenar el Grid con la Base de Datos.
    Belegir.setEnabled(true);

    jt.setEnabled(false);
    jt.setDatos(dtCon1);
    jt.setEnabled(true);

    mensaje("Mataderos Seleccionados ...");

    return true;
  }

  void Belegir_actionPerformed()
  {
    if (jt.isVacio())
    {
      mensaje("No Encontrados Registros para Selecionar");
      jt.requestFocus();
      return;
    }

        // Editar Columna Activa.
    mat_nombT=jt.getValString(1);
    mat_codiT=jt.getValString(0);
    consulta=true;
    matar();
  }

  public void run()
  {
    setEnabled(false);
    rgSelect();
    setEnabled(true);
    jt.setEnabled(true);
    jt.requestFocusInicio();
  }
} // Final de Clase



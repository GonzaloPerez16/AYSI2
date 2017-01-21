package gnu.chu.anjelica.almacen;

/**
 *
 * <p>Titulo: lisaldos </p>
 * <p>Descripción: Listado de Saldos </p>
 * <p>Copyright: Copyright (c) 2005-2016
 *  Este programa es software libre. Puede redistribuirlo y/o modificarlo bajo
 *  los terminos de la Licencia Pública General de GNU según es publicada por
 *  la Free Software Foundation, bien de la versión 2 de dicha Licencia
 *  o bien (según su elección) de cualquier versión posterior.
 *  Este programa se distribuye con la esperanza de que sea útil,
 *  pero SIN NINGUNA GARANTIA, incluso sin la garantía MERCANTIL implícita
 *  o sin garantizar la CONVENIENCIA PARA UN PROPOSITO PARTICULAR.
 *  Véase la Licencia Pública General de GNU para más detalles.
 *  Debería haber recibido una copia de la Licencia Pública General junto con este programa.
 *  Si no ha sido así, escriba a la Free Software Foundation, Inc.,
 *  en 675 Mass Ave, Cambridge, MA 02139, EEUU.
 * </p>
 * <p>Empresa: miSL</p>
 * @author chuchi P
 * @version 1.0 - 20041227
 *
 *  @version 1.1 (20050807) Now only presents products that are 'Vendibles'
 * (look at field 'pro_tiplot' in table 'v_articulo'. But now presents articles that the cost is 0
*
 *
 */
import gnu.chu.Menu.Principal;
import gnu.chu.anjelica.listados.Listados;
import gnu.chu.anjelica.menu;
import gnu.chu.anjelica.pad.MantFamPro;
import gnu.chu.anjelica.pad.pdconfig;
import gnu.chu.camposdb.proPanel;
import gnu.chu.camposdb.sbePanel;
import gnu.chu.controles.*;
import gnu.chu.interfaces.VirtualGrid;
import gnu.chu.interfaces.ejecutable;
import gnu.chu.print.util;
import gnu.chu.utilidades.EntornoUsuario;
import gnu.chu.utilidades.Formatear;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.ventana;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.beans.PropertyVetoException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.border.BevelBorder;
import net.sf.jasperreports.engine.*;


public class lisaldos   extends ventana  implements JRDataSource
{
  CLabel sbe_codiL=new CLabel("Seccion");
  sbePanel sbe_codiE = new sbePanel();
  ArrayList<DatIndiv>  listIndiv=new ArrayList();
  JMenuItem verIndiv = new JMenuItem("Ver Individuos", Iconos.getImageIcon("view_tree"));
  JMenuItem verMvtos = new JMenuItem("Ver Mvtos", Iconos.getImageIcon("view_tree"));

  private double kgVen,kgCom,kgReg=0;
  private CCheckBox pro_cosincE=new CCheckBox("Inc.Costo");
  MvtosAlma mvtosAlm = new MvtosAlma();
//  boolean valDesp;
  boolean cancelado=false;
  char sel='d';
  PreparedStatement ps;
  ResultSet rs;
  ifMvtosClase ifMvtos = new ifMvtosClase();
   IFStockPart ifStk=new IFStockPart(this);
  boolean imprList=false;
  int nLin;
  CButton Bimpr = new CButton(Iconos.getImageIcon("print"));
  String feulin;
  String s;
  private double kilos=0;
  int unid = 0;
  double precio=0;
  CPanel Pprinc = new CPanel();
  CPanel Pdatcon = new CPanel();
    CLinkBox cam_codiE = new CLinkBox();
  CComboBox pro_artconE = new CComboBox();
  CLabel cam_codiL = new CLabel();
  CButton Baceptar = new CButton("Aceptar",Iconos.getImageIcon("check"));
  String camCodi;
  ResultSet dtProd;
  CLabel cLabel2 = new CLabel();
  CLinkBox alm_inicE = new CLinkBox();
    //  int almOri,almFin;
  CLabel cLabel4 = new CLabel();
  CComboBox feulinE = new CComboBox();
  Cgrid jt = new Cgrid(7);
  CLabel cLabel5 = new CLabel();
  proPanel pro_codiE = new proPanel();
  CCheckBox opAjuCosto = new CCheckBox("Aj. Costos");
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  Cgrid jtMv = new Cgrid(7);
  proPanel pro_codmvE = new proPanel();
  CTextField fecsalE = new CTextField(Types.DATE,"dd-MM-yyyy");
  CLabel cLabel6 = new CLabel();
    CCheckBox opIgnDespSVal = new CCheckBox();
    private CPanel Ppie = new CPanel();
    private CLabel kilosL = new CLabel();
    private CTextField kilosE = new CTextField(Types.DECIMAL,"--,---,--9.99");
    private CTextField unidE = new CTextField(Types.DECIMAL,"----,--9");
    private CLabel unidL = new CLabel();
    private CLabel importeL = new CLabel();
    private CTextField importeE = new CTextField(Types.DECIMAL,"----,---,--9.99");
    private CLabel pro_congeL = new CLabel();
    private CLabel ordenL = new CLabel();
    private CComboBox ordenE = new CComboBox();
   

    public lisaldos(EntornoUsuario eu, Principal p)
  {
    EU = eu;
    vl = p.panel1;
    jf = p;
    eje = true;

    setTitulo("Listado de Saldos Almacen");

    try
    {
      if (jf.gestor.apuntar(this))
        jbInit();
      else
        setErrorInit(true);
    }
    catch (Exception e)
    {
      ErrorInit(e);
      setErrorInit(true);
    }
  }

  public lisaldos(menu p, EntornoUsuario eu)
  {

    EU = eu;
    vl = p.getLayeredPane();
    setTitulo("Listado de Saldos Almacen");
    eje = false;

    try
    {
      jbInit();
    }
    catch (Exception e)
    {
      ErrorInit(e);
      setErrorInit(true);
    }
  }

  private void jbInit() throws Exception
  {
    iniciarFrame();
    this.setSize(new Dimension(592, 516));
    this.setVersion("2017-01-21");
    ifMvtos.setSize(new Dimension(475, 325));
    
    ifMvtos.setVisible(false);
    ifMvtos.setClosable(true);
    ifMvtos.setPadre(this);
    ifStk.setVisible(false);
    ifStk.setClosable(false);
    fecsalE.setBounds(new Rectangle(55, 22, 79, 17));
    cLabel6.setBounds(new Rectangle(5, 22, 50, 17));
    cLabel6.setText("En Fecha");
    opIgnDespSVal.setSelected(true);
    opIgnDespSVal.setFocusable(false);
    opIgnDespSVal.setBounds(new Rectangle(140, 22, 70, 17));
    sbe_codiL.setBounds(new Rectangle(215, 22, 50, 17));
    sbe_codiE.setBounds(new Rectangle(268, 22, 40, 17));
    opIgnDespSVal.setText("Ign.Desp.S/Val");
    opIgnDespSVal.setMargin(new Insets(0, 0, 0, 0));
    opIgnDespSVal.setToolTipText("Ignorar Despieces sin valorar para costos");
    cam_codiL.setText("Camara");
    cam_codiL.setBounds(new Rectangle(310, 22, 47, 18));
    cam_codiE.setBounds(new Rectangle(360, 22, 162, 18));
    Pdatcon.setMaximumSize(new Dimension(540, 91));

    this.getLayeredPane().add(ifMvtos);
    this.getLayeredPane().add(ifStk);
    ifMvtos.setLocation(5, 5);
    ifStk.setLocation(5,5);
    Pprinc.setLayout(gridBagLayout1);

    statusBar= new StatusBar(this);
    Bimpr.setPreferredSize(new Dimension(24,24));
    Bimpr.setMinimumSize(new Dimension(24,24));
    Bimpr.setMaximumSize(new Dimension(24,24));

   
    pro_codmvE.setEnabled(false);
    statusBar.add(Bimpr, new GridBagConstraints(9, 0, 1, 2, 0.0, 0.0
                                           , GridBagConstraints.EAST,
                                           GridBagConstraints.VERTICAL,
                                           new Insets(0, 5, 0, 0), 0, 0));

    conecta();
    cLabel2.setText("Almacen");
    cLabel2.setBounds(new Rectangle(5, 45, 60, 17));
    alm_inicE.setAncTexto(30);
    alm_inicE.setBounds(new Rectangle(60, 45, 186, 17));
    Baceptar.setMargin(new Insets(0, 0, 0, 0));
    Baceptar.setText("Aceptar F4");
    cLabel4.setText("Ult. Inventario");
    cLabel4.setBounds(new Rectangle(3, 64, 78, 18));
    feulinE.setBounds(new Rectangle(80, 65, 105, 17));

    confGrid(new ArrayList());

    ArrayList v1=new ArrayList();
    v1.add("Fecha");
    v1.add("Tipo"); // 0 
    v1.add("Kg.Entr.");// 1
    v1.add("Un.Entr");// 2
    v1.add("Kg. Sal");// 3
    v1.add("Un Sal");// 4
    v1.add("Precio");// 5
    jtMv.setCabecera(v1);
    jtMv.setAjustarGrid(true);
    jtMv.setAnchoColumna(new int[]{90,50,80,70,80,70,70});
    jtMv.setAlinearColumna(new int[]{1,1,2,2,2,2,2});
    
    jtMv.setFormatoColumna(2,"---,--9.99");
    jtMv.setFormatoColumna(3,"---,--9");
    jtMv.setFormatoColumna(4,"---,--9.99");
    jtMv.setFormatoColumna(5,"---,--9");   
    jtMv.setFormatoColumna(6,"-,--9.99");
    jtMv.setAjustarGrid(true);
    
    cLabel5.setText("Producto");
    cLabel5.setBounds(new Rectangle(4, 3, 59, 17));
    pro_codiE.setBounds(new Rectangle(63, 2, 380, 17));
    opAjuCosto.setToolTipText("Incluir Ajustes Costos");
    opAjuCosto.setBounds(new Rectangle(452, 2, 75, 17));
    Pprinc.setInputVerifier(null);
    Pdatcon.setMinimumSize(new Dimension(540, 91));
    Pdatcon.setPreferredSize(new Dimension(540, 91));
    Ppie.setMinimumSize(new Dimension(400,25));
    Ppie.setPreferredSize(new Dimension(400,25));
    Ppie.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    Ppie.setLayout(null);
    kilosL.setText("Kilos");
    kilosL.setBounds(new Rectangle(5, 3, 35, 17));
    kilosE.setBounds(new Rectangle(40, 3, 75, 17));
    kilosE.setEditable(false);
    unidE.setBounds(new Rectangle(190, 3, 50, 17));
    unidE.setEditable(false);
    unidL.setBounds(new Rectangle(135, 3, 60, 17));
    unidL.setText("Unidades");
    importeL.setText("Importe");
    importeL.setBounds(new Rectangle(260, 3, 55, 17));
    importeE.setBounds(new Rectangle(310, 3, 85, 17));
    importeE.setEditable(false);
    pro_congeL.setText("Incluir");
    pro_congeL.setBounds(new Rectangle(280, 45, 50, 18));
    ordenL.setText("Orden");
    ordenE.addItem("Producto", "P");
    ordenE.addItem("Familia", "F");

    ordenL.setBounds(new Rectangle(200, 65, 50, 18));
    ordenE.setBounds(new Rectangle(245, 65, 135, 18));
    this.getContentPane().add(statusBar,BorderLayout.SOUTH);

    Pdatcon.setBorder(BorderFactory.createRaisedBevelBorder());
    Pdatcon.setLayout(null);
    
    cam_codiE.setAncTexto(25);
    pro_artconE.setBounds(new Rectangle(332, 45,100, 18));
    pro_artconE.addItem("TODOS","0");
    pro_artconE.addItem("Congelado","1");
    pro_artconE.addItem("NO Congel.","2");
    pro_cosincE.setToolTipText("Incluir Costo Añadido");
    pro_cosincE.setBounds(new Rectangle(435, 45, 90, 18));
    Baceptar.setBounds(new Rectangle(410, 65, 115, 24));
    this.getContentPane().add(Pprinc, BorderLayout.CENTER);
    Pdatcon.add(ordenE, null);
    Pdatcon.add(ordenL, null);
    Pdatcon.add(pro_congeL, null);
    Pdatcon.add(cLabel2, null);
    Pdatcon.add(cLabel5, null);
    Pdatcon.add(pro_codiE, null);
    Pdatcon.add(opAjuCosto, null);
    Pdatcon.add(fecsalE, null);
    Pdatcon.add(cLabel6, null);
    Pprinc.add(jt, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(2, 1, 0, 0), 0, -40));
    Pprinc.add(Pdatcon,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    Ppie.add(importeE, null);
    Ppie.add(importeL, null);
    Ppie.add(unidL, null);
    Ppie.add(unidE, null);
    Ppie.add(kilosE, null);
    Ppie.add(kilosL, null);
    Pprinc.add(Ppie,
               new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                       GridBagConstraints.NONE,
                                      new Insets(0, 0, 0, 0), 0, 0));
    
    StatusBar stBar = new StatusBar(ifMvtos);
    ifMvtos.getContentPane().add(jtMv, BorderLayout.CENTER);
    ifMvtos.getContentPane().add(pro_codmvE, BorderLayout.NORTH);
    ifMvtos.getContentPane().add(stBar,BorderLayout.SOUTH);
    Pdatcon.add(cLabel4, null);
    Pdatcon.add(feulinE, null);
    Pdatcon.add(cam_codiL, null);
    Pdatcon.add(cam_codiE, null);
    Pdatcon.add(pro_artconE, null);
    Pdatcon.add(pro_cosincE,null );
    Pdatcon.add(alm_inicE, null);
    Pdatcon.add(opIgnDespSVal, null);
    Pdatcon.add(sbe_codiL, null);
    Pdatcon.add(sbe_codiE, null);
    Pdatcon.add(Baceptar, null);   
  }

  private void confGrid(ArrayList v)
  {
    v.add("Prod"); // 0
    v.add("Nombre"); // 1
    v.add("Unid"); // 2
    v.add("Kilos"); // 3
    v.add("Precio"); // 4
    v.add("Importe"); // 5
    v.add("Fam"); // 6
    jt.setCabecera(v);
    jt.setAnchoColumna(new int[]{50,150,40,60,60,70,30});
    jt.setAlinearColumna(new int[]{2,0,2,2,2,2,2});
    jt.setMaximumSize(new Dimension(603, 223));
    jt.setMinimumSize(new Dimension(603, 223));
    jt.setFormatoColumna(2,"---9");
    jt.setFormatoColumna(3,"---,--9.99");
    jt.setFormatoColumna(4,"---9.99");
    jt.setFormatoColumna(5,"---,--9.99");
    jt.setAjustarGrid(true);
    jt.getPopMenu().add(verIndiv,1);
    jt.getPopMenu().add(verMvtos,2);
    jt.setToolTipText("Click en boton derecho para más opciones");
    cglisaldos vg=new cglisaldos();
    for (int n=0;n<jt.getColumnCount();n++)
    {
        miCellRender mc= jt.getRenderer(n);
        if (mc==null)
            continue;
        mc.setVirtualGrid(vg);
        mc.setErrBackColor(Color.CYAN);
        mc.setErrForeColor(Color.BLACK);
    }
  }

    @Override
  public void iniciarVentana() throws Exception
  {
    Pdatcon.setDefButton(Baceptar);
    cam_codiE.setFormato(Types.CHAR, "XX", 2);
    cam_codiE.texto.setMayusc(true);
    pro_codiE.iniciar(dtStat,this,vl,EU);
    pro_codmvE.iniciar(dtStat,this,vl,EU);
    sbe_codiE.iniciar(dtStat, this, vl, EU);
    sbe_codiE.setTipo("A");
    sbe_codiE.setValorInt(0);
    sbe_codiE.setAceptaNulo(true);
//    activar(true);

    s="select distinct(rgs_fecha) as cci_feccon from v_regstock as r "+
         " where r.emp_codi = "+EU.em_cod+
         " and tir_afestk='=' "+
         " order by cci_feccon desc ";

     if (dtStat.select(s))
     {
       feulin = dtStat.getFecha("cci_feccon", "dd-MM-yyyy");
       do
       {
         feulinE.addItem(dtStat.getFecha("cci_feccon","dd-MM-yyyy"),dtStat.getFecha("cci_feccon","dd-MM-yyyy"));
       } while (dtStat.next());
     }
     else
     {
       feulin = "01-01-" + EU.ejercicio; // Buscamos desde el principio del a�o.
       feulinE.addItem(feulin);
     }
     feulinE.setText(feulin);
     pdconfig.llenaDiscr(dtStat,cam_codiE,"AC",EU.em_cod);

    alm_inicE.setFormato(true);
    alm_inicE.setFormato(Types.DECIMAL,"#9",2);
    
    
    pdalmace.llenaLinkBox(alm_inicE, dtStat,'*');
//    s="SELECT alm_codi,alm_nomb FROM V_ALMACen ORDER BY alm_codi ";
//    dtStat.select(s);
//    alm_inicE.addDatos(dtStat);
  
    activarEventos();
    fecsalE.setText(Formatear.getFechaAct("dd-MM-yyyy"));
    fecsalE.requestFocus();
  }
  void activarEventos()
  {
    jt.addMouseListener(new MouseAdapter()
      {
          @Override
          public void mouseClicked(MouseEvent e) {
              if (e.getClickCount() < 2 || jt.isVacio())
                  return;            
              mostrarMvtos();
          }
      });
    verIndiv.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (jt.isVacio()) 
             return;
        
          try
          {
              verIndividuos(jt.getValorInt(jt.getSelectedRowDisab(), 0));
          } catch (SQLException ex)
          {
              Error("Error al localizar individuos",ex);
          }
      }
    });
    Baceptar.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
       Baceptar_actionPerformed(threadlisaldos.CONSULTA);
      }
    });
    Bimpr.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Baceptar_actionPerformed(threadlisaldos.LISTA);
      }
    });
    popEspere_BCancelaraddActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        msgEspere("Espere, por favor ... Cancelando Consulta");
        popEspere_BCancelarSetEnabled(false);
        cancelado=true;
      }
    });
    fecsalE.addFocusListener(new FocusAdapter()
    {
            @Override
        public void focusLost(FocusEvent e) {
         try {
          if (fecsalE.isNull() || fecsalE.getError())
              return;
         int nLin=feulinE.getItemCount()-1;
         for (int n=nLin;n>=0;n--)
         {
            if (((String) feulinE.getItemAt(n)).equals(fecsalE.getText()))
            {
                feulinE.setValor((String) feulinE.getItemAt(n));
                return;
            }
            if (Formatear.restaDias((String) feulinE.getItemAt(n), fecsalE.getText())>0)
            {
                feulinE.setValor((String) feulinE.getItemAt(n+1));
                return;
            }
         }
         feulinE.setValor((String) feulinE.getItemAt(0));
         } catch(Exception k )
         {
             Error("Error al buscar fecha",k);
         }
     }
    });
    verMvtos.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (jt.isVacio()) 
             return;
        verMvtos();
      }
    });
   
  }
  void mostrarMvtos() {
    ejecutable prog;
    if ((prog = jf.gestor.getProceso(Comvalm.getNombreClase())) == null)
        return;
    gnu.chu.anjelica.almacen.Comvalm cm = (gnu.chu.anjelica.almacen.Comvalm) prog;

    cm.setProCodi(jt.getValorInt(jt.getSelectedRowDisab(),0));
    cm.setLote(0);
    cm.setIndividuo(0);
    cm.setSerie("");
    cm.setEjercicio(0);
    cm.ejecutaConsulta();
    jf.gestor.ir(cm);
  }
  /**
   * Muestra los individuos desglosados en stock.
   * @param proCodi 
   */
  void verIndividuos(int proCodi) throws SQLException
  {
    listIndiv.clear();
    
    s=getStrSql(proCodi, feulinE.getValor(),fecsalE.getText());
    rs = ps.executeQuery();
    if (!rs.next())
        msgBox("No encontrados invidivuos");
    ifStk.setVisible(true);
    DatIndiv dtInd;
    int row;
    do
    {
        dtInd=new DatIndiv();
        dtInd.setAlmCodi(rs.getInt("alm_codi"));
        dtInd.setProducto(proCodi);
        dtInd.setEjercLot(rs.getInt("pro_ejelot"));
        dtInd.setSerie(rs.getString("pro_serlot"));
        dtInd.setLote(rs.getInt("pro_numlot"));
        dtInd.setNumind(rs.getInt("pro_indlot"));
       
        row=listIndiv.indexOf(dtInd);
        if (row>=0)
            dtInd=listIndiv.get(row);
        dtInd.setCanti((rs.getString("tipmov").equals("=")?0:dtInd.getCanti())+
            rs.getDouble("canti")* (rs.getString("tipmov").equals("S")?-1:1 ));
        dtInd.setNumuni((rs.getString("tipmov").equals("=")?0:dtInd.getNumuni())+
            rs.getInt("unid")* (rs.getString("tipmov").equals("S")?-1:1 ));
         if (row>=0)
             listIndiv.set(row,dtInd);
         else
            listIndiv.add(dtInd);
    } while (rs.next());
    ifStk.iniciar(proCodi,feulinE.getValor(),fecsalE.getText(), listIndiv);
//    for (DatIndiv dtInd1 : listIndiv)
//    {
//        if (dtInd1.getCanti()==0)
//            continue;
//        System.out.println("indiv: "+dtInd1.getProducto()+" "+
//            dtInd1.getEjercLot()+ dtInd1.getSerie()+dtInd1.getLote()+" "+dtInd1.getNumind()+ ":"+
//            dtInd1.getCanti()+" kg "+dtInd1.getNumuni());
//    }

  }
  /**
   * LLamada cuando se hace doble click en una linea.
   * Muestra un detalle de los movimientos realizados.
   */
  void verMvtos()
  {
      ifMvtos.setVisible(true);
      int proCodi = jt.getValorInt(jt.getSelectedRowDisab(), 0);
      String fecinv = fecsalE.getText();
      pro_codmvE.setValorInt(proCodi);
      jtMv.removeAllDatos();
      ArrayList v = new ArrayList();
      try
      {
          getStrSql(proCodi, feulin, fecinv);
//   debug("verMvtos: "+s);
          rs = ps.executeQuery();
          if (!rs.next())
              return;
          dtCon1.setResultSet(rs);

          unid = 0;
          do
          {
              v.clear();
              v.add(dtCon1.getFecha("fecmov","dd-MM-yy HH:mm"));
              switch (dtCon1.getString("tipmov"))
              {
                  case "=":
                      v.add("=");
                      v.add(dtCon1.getString("canti"));
                      v.add(dtCon1.getString("unid"));
                      v.add(0);
                      v.add(0);                                        
                      break;
                  case "E":  
                    v.add(dtCon1.getString("sel"));
                    v.add(dtCon1.getString("canti"));
                    v.add(dtCon1.getString("unid"));
                    v.add(0);
                    v.add(0);
                    break;
                  default:              
                    v.add(dtCon1.getString("sel"));
                    v.add(0);
                    v.add(0);
                    v.add(dtCon1.getString("canti"));
                    v.add(dtCon1.getString("unid"));
              }
              v.add(dtCon1.getString("precio"));
              jtMv.addLinea(v);
          } while (dtCon1.next());

      } catch (SQLException k)
      {
          Error("Error al ver Mvtos desglosados", k);
      }
  }
  void Baceptar_actionPerformed(int opcion)
  {
    ps=null;
    ifMvtos.setVisible(false);
    ifStk.setVisible(false);
    if (fecsalE.isNull())
    {
      mensajeErr("Introduzca Fecha de Saldo");
      return;
    }
    camCodi=cam_codiE.getText().trim();
    if (camCodi.equals(""))
      camCodi=null;
    if (camCodi!=null)
    {
      camCodi = camCodi.replace('*', '%');
      if (camCodi.equals("*") || camCodi.equals("**"))
        camCodi = null;
    }

//    almOri=Integer.parseInt(alm_inicE.getText().trim());
//    almFin=Integer.parseInt(alm_finalE.getText().trim());
    
    feulin = feulinE.getValor();
   
    threadlisaldos th =new threadlisaldos(this,opcion);
    th.start();
  }

  boolean consultar()
  {
    msgEspere("Calculando Saldos...");
    popEspere_BCancelarSetEnabled(true);
    
    cancelado=false;
    mensaje("A esperar que estoy  buscando datitos ...");
//    activar(false);
    try
    {
      mvtosAlm.setUsaDocumentos(false);
      mvtosAlm.setIncUltFechaInv(fecsalE.getText().equals(feulinE.getText()));
      mvtosAlm.setIgnDespSinValor(opIgnDespSVal.isSelected());
      mvtosAlm.setAlmacen(alm_inicE.getValorInt());
      mvtosAlm.setEntornoUsuario(EU);
      mvtosAlm.setIncAjusteCostos(opAjuCosto.isSelected());
      mvtosAlm.setSoloInventario(fecsalE.getText().equals(feulinE.getText()));
      
      if (!alm_inicE.isNull())
          mvtosAlm.setIncluyeSerieX(true);
      mvtosAlm.iniciarMvtos(feulin,fecsalE.getText(),dtCon1);
//      mvtosAlm.setDesglIndiv(true);
      char orden=ordenE.getValor().charAt(0);
      jt.removeAllDatos();
      s = "SELECT a.*,f.fpr_nomb FROM V_ARTICULO as a left join v_famipro as f on f.fpr_codi = a.fam_codi where 1=1  " +
          (camCodi != null ? " and a.cam_codi= '" + camCodi + "'" : "") +
          (pro_codiE.isNull()?"":" and a.pro_codi = "+pro_codiE.getValorInt())+
          (pro_artconE.getValorInt()==0?"": " and a.pro_artcon "+(pro_artconE.getValorInt()==1?"!":"")+"=0")+
          (sbe_codiE.getValorInt()==0?"": " and a.sbe_codi = "+sbe_codiE.getValorInt())+
          " and a.pro_tiplot='V' "+
          " ORDER BY "+(orden=='F'?" fam_codi,":"")+
          " pro_codi ";
      dtProd = stUp.executeQuery(s);
      int unidT=0;
      double kilosT=0,importeT=0;
      double kgVenT=0,kgComT=0,kgRegT=0;
     
      ArrayList<ArrayList> datos=new ArrayList();
      int famCodi=0;
      while (next())
      {
        if (cancelado)
        {
          mensajeErr("Consulta Cancelada ...");
          
          mensaje("");
          resetMsgEspere();
//          activar(true);
          pro_codiE.requestFocus();
          return false;
        }
        if (famCodi!=dtProd.getInt("fam_codi") && orden=='F' )
        {
           ArrayList v1 = new ArrayList();  
           v1.add("");
           v1.add("Fam: "+dtProd.getString("fpr_nomb"));
           v1.add("" );
           v1.add("" );
           v1.add("" );
           v1.add("" );
           v1.add("");
           datos.add(v1);
           famCodi=dtProd.getInt("fam_codi");
        }
        ArrayList v = new ArrayList();
        v.add(dtProd.getString("pro_codi"));
        v.add(dtProd.getString("pro_nomb"));
        v.add( unid);
        v.add(kilos);
        v.add( precio);
        v.add(kilos * precio);
        v.add(famCodi);
        datos.add(v);
        kilosT+=kilos;
        unidT+=unid;
        kgComT+=kgCom;
        kgVenT+=kgVen;
        kgRegT+=+kgReg;
        importeT+=(kilos * precio);
      }
      jt.setDatos(datos);
      jt.requestFocusInicio();
      kilosE.setValorDec(kilosT);
      unidE.setValorDec(unidT);
      importeE.setValorDec(importeT);
//        System.out.println("Kilos venta: "+kgVenT+" Kg.Compra: "+kgComT+" Kg. Reg:"+kgRegT);
      resetMsgEspere();
    //  mensaje("Pulse Doble click en una linea para ver los movimientos");
      mensajeErr("Consulta .... Generada");
//      activar(true);
      Pdatcon.resetCambio();
      pro_codiE.requestFocus();
    }
    catch (SQLException | ParseException | JRException ex)
    {
      Error("Error al buscar Productos", ex);
    }
    return true;
  }

  void listar()
  {
    try
    {
      msgEspere("Generando Listado.. espere, por favor");
      popEspere_BCancelarSetEnabled(false);
      if (Pdatcon.hasCambio() || cancelado)
      {
        if (! consultar())
            return;
      }
      mensaje("Generando listado ...");
//      activar(false);
      
      nLin=-1;
      imprList=true;
      JasperReport jr = Listados.getJasperReport(EU, "lisaldos");

      HashMap mp = Listados.getHashMapDefault();
      mp.put("fecini",feulin);
      mp.put("fecsal",fecsalE.getText());
      mp.put("ordenFam", ordenE.getValor().equals("F"));
      if (camCodi==null)
        mp.put("camara", "*TODAS*");
      else
        mp.put("camara",
               cam_codiE.getText() + " -> " + cam_codiE.getTextCombo());

      JasperPrint jp = JasperFillManager.fillReport(jr, mp, this);
      mensaje("");


      if (util.printJasper(jp, EU))
            mensajeErr("Listado .... Generado");
      
      mensaje("");
      
      resetMsgEspere();

      imprList=false;
    }
    catch (JRException | PrinterException ex)
    {
      Error("Error al buscar Productos",ex);
    }
  }

    @Override
  public boolean next() throws JRException
  {
    try {
      if (imprList)
      {
        nLin++;
        if (nLin>=jt.getRowCount())
            return false;
        if (jt.getValString(nLin,0).equals(""))
            nLin++;
        
        return true;
      }
      if (! dtProd.next())
        return false;
      setMensajePopEspere("Ejecutando Consulta de Saldos\nTratando producto "+dtProd.getInt("pro_codi"),false);

      precio=0;
      while (true)
      {
        
        if (! mvtosAlm.calculaMvtos(dtProd.getInt("pro_codi"), dtCon1, dtStat, null,null))
        {
          if (! dtProd.next())
            return false;
          continue;
        }
        kilos=mvtosAlm.getKilosStock();
        unid=mvtosAlm.getUnidStock();
        kgCom=mvtosAlm.getKilosCompra();
        kgVen=mvtosAlm.getKilosVenta();
        kgReg=mvtosAlm.getKilosRegul();
        precio = mvtosAlm.getPrecioStock()+
                (pro_cosincE.isSelected()?dtProd.getDouble("pro_cosinc"):0) ;
                //getPreMed(dtProd.getInt("pro_codi"), fecsalE.getText());
//        if (precio != 0 && (kilos >= 1 || kilos <= -1))
        if ( (kilos >= 1 || kilos <= -1))
          return true;
        if (! dtProd.next())
          return false;
      }

    } catch (Exception k)
    {
      Error("Error en Next: ",k);
      throw new JRException(k.getMessage());
    }
  }

    @Override
  public Object getFieldValue(JRField field) throws JRException
  {
    try
    {
      if (field.getName().equals("pro_codi"))
        return jt.getValorInt(nLin,0);
       if (field.getName().equals("fam_codi"))
        return jt.getValorInt(nLin,6);
       if (field.getName().equals("fpr_nomb"))
        return MantFamPro.getNombreFam(jt.getValorInt(nLin,6),dtStat);
      if (field.getName().equals("pro_nomb"))
        return jt.getValString(nLin,1);
      if (field.getName().equals("kilos"))
        return jt.getValorDec(nLin,3);
      if (field.getName().equals("unid"))
        return jt.getValorInt(nLin,2);
      if (field.getName().equals("precio"))
        return jt.getValorDec(nLin,4);
      if (field.getName().equals("importe"))
        return jt.getValorDec(nLin,5);
      throw new JRException("Field: "+field.getName()+" NO valido");
    }
    catch (NumberFormatException n)
    { // Para cuando salen infinitos y cosas asi
      return (double) 0;
    }
    catch (SQLException | JRException k)
    {
      Error("Error en getFieldValue: ("+field.getName()+")", k);
      throw new JRException(k.getMessage());
    }

  }
  

  String getStrSql(int proCodi, String fecini, String fecfin) throws SQLException
  {
    
    if (ps==null)
    {
       s="SELECT  mvt_tipdoc as sel, mvt_tipo as tipmov,mvt_time as fecmov, alm_codi, pro_codi,"
           + "pro_ejelot,pro_serlot,pro_numlot,pro_indlot,"+
            " "+        
            " mvt_canti as canti,mvt_prec as precio "+
            ", mvt_unid as unid " +
            " from mvtosalm where "+
            " mvt_canti <> 0 "+
            " AND pro_codi = ?" +
              (alm_inicE.getValorInt() == 0 ? "" : " and alm_codi = " + alm_inicE.getValorInt()) +
             " AND mvt_time::date >= TO_DATE('" + fecini + "','dd-MM-yyyy') " +
          "   and  mvt_time::date <= TO_DATE('" + fecfin + "','dd-MM-yyyy') "+
          " UNION all " + // Inventarios
          " select 'RE' as sel,tir_afestk as tipmov,rgs_fecha as fecmov,alm_codi, pro_codi, eje_nume as pro_ejelot,"
           + "pro_serie as pro_serlot,pro_nupar as pro_numlot, pro_numind as pro_indlot,"+
          " r.rgs_kilos as canti,r.rgs_prregu as precio,1 as unid  " +
          " FROM v_regstock as r WHERE " +
          " tir_afestk = '='"+
          " and rgs_kilos <> 0" +
          " and rgs_trasp != 0 "+
          (alm_inicE.getValorInt() == 0 ? "" : " and alm_codi = " + alm_inicE.getValorInt()) +
          " AND r.pro_codi = ? " +
          " AND r.rgs_fecha::date >= TO_DATE('" + fecini + "','dd-MM-yyyy') " +
          " and r.rgs_fecha::date <= TO_DATE('" + fecfin + "','dd-MM-yyyy') ";
      s += " ORDER BY 3,2 desc"; // FECHA y tipo
      ps=dtCon1.getConexion().prepareStatement(dtCon1.getStrSelect(s));
    }
    ps.setInt(1,proCodi);
    ps.setInt(2,proCodi);   
    return s;
  }

    @Override
  public void matar(boolean cerrarConexion)
 {
   if (muerto)
     return;
   if (ifMvtos!=null)
   {
     ifMvtos.setVisible(false);
     ifMvtos.dispose();
   } 
   ifStk.setVisible(false);
   ifStk.dispose();
   super.matar(cerrarConexion);
 }

}

class threadlisaldos extends Thread
{
  final static int CONSULTA=1;
  final static int LISTA=2;
  lisaldos lisal;
  int opcion; 
  
  public threadlisaldos(lisaldos lisal,int opcion)
  {
    this.lisal=lisal;
    this.opcion=opcion;
  }
    @Override
  public void run()
  {
   
    this.setPriority(Thread.MAX_PRIORITY-2);
    if (opcion==CONSULTA)
      lisal.consultar();
    if (opcion==LISTA)
      lisal.listar();
    lisal.mensaje("");
  }
}
class cglisaldos implements VirtualGrid
{
 @Override
 public boolean getColorGrid(int row, int col, Object valor, boolean selecionado, String nombreGrid)
 {
     return  (col==1 && ((String) valor).startsWith("Fam:"));             
 }
}

class  ifMvtosClase extends ventana
{
      lisaldos papa;
      public void setPadre(lisaldos padre)
      {
          papa=padre;
          this.setTitle("Consulta Mvtos de Prod.");
      }
       @Override
        public void matar()
        {
          setVisible(false);
    
          papa.setEnabled(true);
          papa.setFoco(null);
          try {
              papa.setSelected(true);
          } catch (PropertyVetoException k){}
        }  
  }

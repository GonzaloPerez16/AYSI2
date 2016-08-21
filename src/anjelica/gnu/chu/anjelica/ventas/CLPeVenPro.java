package gnu.chu.anjelica.ventas;
/**
 *
 * <p>Título: CLPeVenPro </p>
 * <p>Descripción: Consulta/Listado Pedidos de Ventas Agrupados por Productos</p>
 * <p>Copyright: Copyright (c) 2005-2016
 *  Este programa es software libre. Puede redistribuirlo y/o modificarlo bajo
 *  los términos de la Licencia Publica General de GNU según es publicada por
 *  la Free Software Foundation, bien de la versión 2 de dicha Licencia
 *  o bien (según su elección) de cualquier versión posterior.
 *  Este programa se distribuye con la esperanza de que sea útil,
 *  pero SIN NINGUNA GARANTÍA, incluso sin la garantía MERCANTIL implícita
 *  o sin garantizar la CONVENIENCIA PARA UN PROPÓSITO PARTICULAR.
 *  Véase la Licencia Pública General de GNU para más detalles.
 *  Debería haber recibido una copia de la Licencia Pública General junto con este programa.
 *  Si no ha sido así, escriba a la Free Software Foundation, Inc.,
 *  en 675 Mass Ave, Cambridge, MA 02139, EEUU.
 * </p>
 * @author chuchiP
 * @version 1.0
 *
 */
import gnu.chu.Menu.Principal;
import gnu.chu.anjelica.almacen.pdalmace;
import gnu.chu.anjelica.listados.Listados;
import gnu.chu.anjelica.pad.MantRepres;
import gnu.chu.anjelica.pad.pdconfig;
import gnu.chu.controles.StatusBar;
import gnu.chu.sql.DatosTabla;
import gnu.chu.utilidades.EntornoUsuario;
import gnu.chu.utilidades.Formatear;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.ventana;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;


public class CLPeVenPro extends ventana implements  JRDataSource
{
    final static int JT_CANTI = 2;
    final static int JT_TIPO = 3;
    int linProd = 0;
    int linClien = 0;
    boolean swRotoProd;
    String condWhere;
    String s;

    String ARG_REPCODI = "";
    String ARG_SBECODI = "";
 
    
    public CLPeVenPro(EntornoUsuario eu, Principal p) {
        this(eu, p, null);
    }

    public CLPeVenPro(EntornoUsuario eu, Principal p, HashMap<String,String> ht) {
        EU = eu;
        vl = p.panel1;
        jf = p;
        eje = true;

        try {
            if (ht != null) {
                 if (ht.get("repCodi") != null) 
                    ARG_REPCODI = ht.get("repCodi");
                
                if (ht.get("sbeCodi") != null)
                    ARG_SBECODI = ht.get("sbeCodi");
              
            }
            setTitulo("Cons/List. Productos Pedidos de Ventas");
            if (jf.gestor.apuntar(this)) {
                jbInit();
            } else {
                setErrorInit(true);
            }
        } catch (Exception e) {
           ErrorInit(e);
        }
    }

    public CLPeVenPro(gnu.chu.anjelica.menu p, EntornoUsuario eu, HashMap <String,String> ht) {
        EU = eu;
        vl = p.getLayeredPane();
        eje = false;

        try {
            if (ht != null) {
                if (ht.get("repCodi") != null) 
                    ARG_REPCODI = ht.get("repCodi");
                
                if (ht.get("sbeCodi") != null)
                    ARG_SBECODI = ht.get("sbeCodi");
            }
             setTitulo("Cons/List. Productos Pedidos de Ventas");

            jbInit();
        } catch (Exception e) {
           ErrorInit(e);
        }
    }
    
    private void jbInit() throws Exception {
        statusBar = new StatusBar(this);

        iniciarFrame();

        this.setVersion("2016-08-04");

        initComponents();
        this.setSize(new Dimension(730, 535));
        this.getContentPane().add(statusBar, BorderLayout.SOUTH);

        conecta();
    }
    @Override
 public void iniciarVentana() throws Exception
  {
    confJtPro();
    confJtCli();
    confJtPed();
    statusBar.add(Bimpri, new GridBagConstraints(9, 0, 1, 2, 0.0, 0.0
                                           , GridBagConstraints.EAST,
                                           GridBagConstraints.VERTICAL,
                                           new Insets(0, 5, 0, 0), 0, 0));
    Pcondi.setDefButton(Baceptar);
    tla_codiE.setFormato(Types.DECIMAL, "#9");

    Pprinc.setButton(KeyEvent.VK_F4, Baceptar);
     emp_codiE.iniciar(dtStat, this, vl, EU);
     
     sbe_codiE.iniciar(dtStat, this, vl, EU);
     sbe_codiE.setFieldEmpCodi(emp_codiE.getTextField());
     sbe_codiE.setAceptaNulo(true);
     sbe_codiE.setValorInt(0);
   
    s = "SELECT tla_codi,tla_nomb FROM tilialca order by tla_codi";
    dtStat.select(s);
    tla_codiE.addDatos(dtStat);
    tla_codiE.addDatos("99", "Definido Usuario");
    tla_codiE.setValorInt(99);

 
    pro_codiE.iniciar(dtStat, this, vl, EU);
    MantRepres.llenaLinkBox(rep_codiE, dtCon1);
    pdconfig.llenaDiscr(dtStat, zon_codiE, pdconfig.D_ZONA,EU.em_cod);
    pdconfig.llenaDiscr(dtStat, rut_codiE, pdconfig.D_RUTAS, EU.em_cod);
     rut_codiE.setCeroIsNull(true);
    pvc_feciniE.setAceptaNulo(true);
    pvc_feciniE.setAceptaNulo(true);
    
    pdalmace.llenaLinkBox(alm_codiE, dtStat);
 
    alm_codiE.setText("");
    
    pvc_feciniE.setText(Formatear.sumaDias(Formatear.getDateAct(), -7));
    pvc_fecfinE.setText(Formatear.getFechaAct("dd-MM-yyyy"));
    emp_codiE.setValorInt(EU.em_cod);
    activarEventos();
  }
  void activarEventos()
  {
    Bimpri.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Bimpri_actionPerformed();
      }
    });

    Baceptar.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent e)
      {
        Baceptar_actionPerformed();
      }
    });
    jtProd.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting() || ! jtProd.isEnabled() || jtProd.isVacio() )
          return;
        try {
          verDatCli(jtProd.getValString(0));
        } catch (Exception k)
        {
          Error("Error al Ver desglose de clientes",k);
        }
      }
    });

    jtCli.addListSelectionListener(new ListSelectionListener()
    {
      @Override
      public void valueChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting() || ! jtCli.isEnabled() || jtCli.isVacio() )
          return;
        try {
          verDatPed(jtProd.getValString(0), jtCli.getValorInt(0));
         } catch (Exception k)
        {
          Error("Error al Ver desglose de Pedidos",k);
        }

      }
    });
  }
  void Baceptar_actionPerformed()
  {
    try
    {
      if (! iniciarCons(true))
        return;
      do
      {
        ArrayList v=new ArrayList();
        v.add((tla_codiE.getValorInt()==99?"":"L")+dtCon1.getString("pro_codi")); // 0
        v.add(dtCon1.getString("pro_nomb")); // 1
        v.add(dtCon1.getString("pvl_canti")); // 2
        v.add(dtCon1.getString("pvl_tipo")); // 2
        jtProd.addLinea(v);
      } while (dtCon1.next());
      jtProd.requestFocusInicio();
      jtProd.setEnabled(true);
      jtCli.setEnabled(true);
      jtPed.setEnabled(true);
      verDatCli(jtProd.getValString(0,0));
    }
    catch (Exception k)
    {
      Error("Error al buscar pedidos", k);
    }
  }
  void verDatCli(String producto) throws Exception
  {
    if (! jtProd.isEnabled() ||  ! jtCli.isEnabled() || producto == null)
      return;
    producto=producto.trim();
    if (producto.equals(""))
      return;
    int proCodi=0;
    boolean swGrupo=producto.startsWith("L");
    if (swGrupo)
      proCodi=Integer.parseInt(producto.substring(1));
    else
      proCodi=Integer.parseInt(producto);
    s="SELECT cl.cli_codi, cl.cli_nomb, sum(pvl_canti) as pvl_canti,pvl_tipo FROM v_pedven as p, clientes as cl "+
        (swGrupo?", tilialpr as t ":"")+
        condWhere+
        (swGrupo?" and tla_orden = "+proCodi+" and p.pro_codi = t.pro_codi ":
         " and p.pro_codi = "+proCodi)+
        " and cl.cli_codi = p.cli_codi "+
        " group by pvl_tipo,cl.cli_codi,cl.cli_nomb "+
        " order by cl.cli_codi ";
//    debug(s);
    jtCli.setEnabled(false);
    jtCli.removeAllDatos();
    if (! dtCon1.select(s))
      return;
    do
    {
      ArrayList v=new ArrayList();
      v.add(dtCon1.getString("cli_codi"));
      v.add(dtCon1.getString("cli_nomb"));
      v.add(dtCon1.getString("pvl_canti")+dtCon1.getString("pvl_tipo"));
      jtCli.addLinea(v);
    } while (dtCon1.next());
    jtCli.requestFocusInicio();
    jtCli.setEnabled(true);
    verDatPed(producto,jtCli.getValorInt(0,0));
  }
  private boolean iniciarCons(boolean ejecSelect) throws SQLException, ParseException
  {
    
    if (pvc_feciniE.getError())
    {
      mensajeErr("Fecha Entrega INICIAL no es valida");
      pvc_feciniE.requestFocus();
      return false;
    }
    if (pvc_fecfinE.getError())
    {
      mensajeErr("Fecha Entrega FINAL no es valida");
      pvc_fecfinE.requestFocus();
      return false;
    }

    if (emp_codiE.getValorInt()==0)
    {
      mensajeErr("Debe introducir una empresa");
      emp_codiE.requestFocus();
      return false;
    }
    if (! ejecSelect)
      return true; // No ejecutar select, solo comprobar campos.

    s = "SELECT "+
        (tla_codiE.getValorInt()==99?"ar.pro_codi , ar.pro_nomb ":
         "t.tla_orden as pro_codi , gr.pro_desc as pro_nomb ")+
        " ,sum(pvl_canti) as pvl_canti,pvl_tipo as pvl_tipo FROM v_pedven as  p,v_cliente as cl " +
        (tla_codiE.getValorInt()==99?",v_articulo as ar ":", tilialpr as t,tilialgr as gr ");

    condWhere=" where   (p.avc_ano = 0 or pvc_cerra=0) "+
        " and pvc_confir='S' "+
        " and p.cli_codi = cl.cli_codi "+
        (pvc_feciniE.isNull()?"":" AND pvc_fecent >= {ts '" + pvc_feciniE.getFechaDB() + "'}") +
        (pvc_fecfinE.isNull()?"": " and pvc_fecent <= {ts '" + pvc_fecfinE.getFechaDB() + "'}")+
        (alm_codiE.getValorInt()==0?"":" and p.alm_codi = " + alm_codiE.getValorInt())+
        (sbe_codiE.getValorInt()==0?"":" and cl.sbe_codi = " + alm_codiE.getValorInt())+
        (zon_codiE.isNull()?"":" and cl.zon_codi = '"+zon_codiE.getText()+"'")+
        (rep_codiE.isNull()?"":" and cl.rep_codi = '"+rep_codiE.getText()+"'")+
        (rut_codiE.isNull()?"": " and cli_ruta ='"+rut_codiE.getText()+"'")+
        " AND p.emp_codi = " + emp_codiE.getValorInt();
    s+= condWhere+
       (tla_codiE.getValorInt()==99?"  and ar.pro_codi = p.pro_codi ":
       " and p.pro_codi = t.pro_codi ")+
        (tla_codiE.getValorInt()!=99 || pro_codiE.getValorInt()==0?"": " and p.pro_codi = "+pro_codiE.getValorInt())+
        (tla_codiE.getValorInt()==99?"":" and gr.tla_codi = t.tla_codi "+
        " and gr.tla_orden = t.tla_orden and t.tla_codi="+tla_codiE.getValorInt())+
       " group by pvl_tipo,   "+
       (tla_codiE.getValorInt()==99?"ar.pro_codi , ar.pro_nomb ":
         "t.tla_orden , gr.pro_desc ")+
        (tla_codiE.getValorInt()==99?" order by ar.pro_codi ": " order by t.tla_orden ");
     debug(s);
    jtProd.setEnabled(false);
    jtCli.setEnabled(false);
    jtProd.removeAllDatos();
    jtCli.removeAllDatos();
    jtPed.removeAllDatos();
    if (!dtCon1.select(s))
    {
      mensajeErr("NO hay PEDIDOS que cumplan estas condiciones");
      tla_codiE.requestFocus();
      return false;
    }
    return true;
  }

  String getAlmNomb(int almCodi, DatosTabla dt) throws SQLException, java.text.ParseException
  {
   s="SELECT alm_nomb FROM v_almacen WHERE alm_codi = "+almCodi;
   if (! dt.select(s))
     return "**ALMACEN: "+almCodi+" NO ENCONTRADO**";

   return dt.getString("alm_nomb");
  }

  private void confJtPro()
  {
    ArrayList v=new ArrayList();
    v.add("Codigo"); // 0
    v.add("Nombre Producto"); // 1
    v.add("Canti.");// 2
    v.add("Tipo");// 3
    jtProd.setCabecera(v);
    jtProd.setMaximumSize(new Dimension(333, 345));
    jtProd.setMinimumSize(new Dimension(333, 345));
    jtProd.setPreferredSize(new Dimension(333, 345));
    jtProd.setBuscarVisible(false);
    jtProd.setAnchoColumna(new int[]{50,160,70,50});
    jtProd.setAlinearColumna(new int[]{2,0,2,0});
    jtProd.setFormatoColumna(2,"##9.99");
  }
    private void confJtCli() throws Exception
   {
     ArrayList v = new ArrayList();
     v.add("Cliente"); // 0
     v.add("Nombre Cliente"); // 1
     v.add("Canti"); // 2
     jtCli.setCabecera(v);
     jtCli.setMaximumSize(new Dimension(322, 208));
    jtCli.setMinimumSize(new Dimension(322, 208));
    jtCli.setPreferredSize(new Dimension(322, 208));
    jtCli.setBuscarVisible(false);
     jtCli.setPuntoDeScroll(50);
     jtCli.setAnchoColumna(new int[]{60, 160, 70});
     jtCli.setAlinearColumna(new int[] {2, 0, 2});
     jtCli.setFormatoColumna(2,"----9");
   }
   private void confJtPed() throws Exception
   {
     ArrayList v = new ArrayList();
     v.add("Pedido"); // 0
     v.add("Fec.Entr."); // 1
     v.add("Unid."); // 2
     v.add("Precio"); // 3
     v.add("CP");  // 4 Confirmado Precio
     v.add("Proveed"); // 5 Nombre Proveedor
     v.add("Fec.Cad"); // 6
     v.add("Conf"); // 7 Pedido Confirmado
     v.add("Alm."); // 8
     v.add("Comentario"); // 9 Comentario
     jtPed.setCabecera(v);
     jtPed.setAnchoColumna(new int[]{70,70,40,50,30,100,70,30,30,100});
     jtPed.setAlinearColumna(new int[]{2,1,2,2,1,0,1,2,0,0});
     jtPed.setFormatoColumna(2,"----9");
     jtPed.setFormatoColumna(3,"---9.99");
     jtPed.setFormatoColumna(4,"BSN");
     jtPed.setFormatoColumna(7,"BSN");
   }
   void verDatPed(String producto,int cliCodi) throws Exception
   {
     if (! jtPed.isEnabled() || ! jtCli.isEnabled())
       return;
     int proCodi = 0;
     boolean swGrupo = producto.startsWith("L");
     if (swGrupo)
       proCodi = Integer.parseInt(producto.substring(1));
     else
       proCodi = Integer.parseInt(producto);

     s="SELECT p.*,pv.prv_nomb FROM v_cliente as cl,v_pedven as p"+
       "  left join v_proveedo as pv on pv.prv_codi = p.prv_codi  "+
      (swGrupo?", tilialpr as t ":"")+
      condWhere+
      (swGrupo?" and tla_orden = "+proCodi+" and p.pro_codi = t.pro_codi ":
       " and p.pro_codi = "+proCodi)+
       " AND p.cli_codi = "+cliCodi+
       " order by p.emp_codi,p.eje_nume,p.pvc_nume ";
//   debug(s);
       jtPed.removeAllDatos();
       if (! dtCon1.select(s))
       {
         mensajeErr("NO ENCONTRADOS PEDIDOS PARA ESTE CLIENTE Y PRODUCTO");
         return;
       }

       do
       {
         ArrayList v=new ArrayList();
         v.add(dtCon1.getString("eje_nume")+"/"+dtCon1.getString("pvc_nume"));
         v.add(dtCon1.getFecha("pvc_fecent","dd-MM-yy"));
         v.add(dtCon1.getString("pvl_canti")+dtCon1.getString("pvl_tipo"));
         v.add(dtCon1.getString("pvl_precio"));
         v.add(dtCon1.getInt("pvl_precon") != 0);
         v.add(dtCon1.getString("prv_nomb"));
         v.add(dtCon1.getFecha("pvl_feccad","dd-MM-yy"));
         v.add(dtCon1.getString("pvc_confir").equals("S"));
         v.add(dtCon1.getString("alm_codi"));
         v.add(dtCon1.getString("pvl_comen"));
         jtPed.addLinea(v);
       } while (dtCon1.next());
       jtPed.requestFocusInicio();
   }


   void Bimpri_actionPerformed()
    {
      try {
        if (jtProd.isVacio())
        {
          mensajeErr("Realize primero una consulta");
          return;
        }
        linProd=-1;
        linClien=0;
        jtProd.requestFocusInicio();
        swRotoProd=true;
        java.util.HashMap mp = Listados.getHashMapDefault();
        mp.put("fecini",pvc_feciniE.getDate());
        mp.put("fecfin",pvc_fecfinE.getDate());

        JasperReport jr;
        jr = Listados.getJasperReport(EU, "relpevepr");

        JasperPrint jp = JasperFillManager.fillReport(jr, mp, this);
        gnu.chu.print.util.printJasper(jp, EU);

        mensajeErr("Relacion Pedido Ventas ... IMPRESO ");
      }
      catch (Exception k)
      {
        Error("Error al imprimir Pedido Venta", k);
      }
    }

    public boolean next() throws JRException
    {
      if (linProd<0)
      {
        linProd = 0;
        return true;
      }
      if (linClien+1<jtCli.getRowCount())
      {
        swRotoProd=false;
        linClien++;
        return true;
      }
      if (linProd+1 >= jtProd.getRowCount())
        return false;
      linProd++;
      jtProd.requestFocus(linProd,0);
      swRotoProd=true;
      linClien=0;
      return true;
    }

  @Override
    public Object getFieldValue(JRField jRField) throws JRException
    {
      String campo = jRField.getName();
      if (campo.equals("pro_codi"))
        return swRotoProd?jtProd.getValString(linProd,0):null;
      if (campo.equals("pro_nomb"))
        return swRotoProd?jtProd.getValString(linProd,1):null;
      if (campo.equals("pvl_canti"))
        return swRotoProd?jtProd.getValorDec(linProd,JT_CANTI):null;
      if (campo.equals("pvl_tipo"))
        return swRotoProd?jtProd.getValString(linProd,JT_TIPO):null;
      if (campo.equals("cli_nomb"))
        return jtCli.getValString(linClien,1);
      if (campo.equals("unidcli"))
        return jtCli.getValorInt(linClien,2);

      throw new JRException("Campo: "+campo+ " No definido");
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

        Bimpri = new gnu.chu.controles.CButton(Iconos.getImageIcon("print"));
        Pprinc = new gnu.chu.controles.CPanel();
        Pcondi = new gnu.chu.controles.CPanel();
        cLabel4 = new gnu.chu.controles.CLabel();
        emp_codiE = new gnu.chu.camposdb.empPanel();
        cLabel5 = new gnu.chu.controles.CLabel();
        sbe_codiE = new gnu.chu.camposdb.sbePanel();
        cLabel6 = new gnu.chu.controles.CLabel();
        pvc_feciniE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        cLabel7 = new gnu.chu.controles.CLabel();
        pvc_fecfinE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        cLabel16 = new gnu.chu.controles.CLabel();
        rep_codiE = new gnu.chu.controles.CLinkBox();
        zon_codiE = new gnu.chu.controles.CLinkBox();
        cLabel18 = new gnu.chu.controles.CLabel();
        Baceptar = new gnu.chu.controles.CButton(Iconos.getImageIcon("check"));
        cLabel21 = new gnu.chu.controles.CLabel();
        alm_codiE = new gnu.chu.controles.CLinkBox();
        cLabel22 = new gnu.chu.controles.CLabel();
        rut_codiE = new gnu.chu.controles.CLinkBox();
        cLabel1 = new gnu.chu.controles.CLabel();
        pro_codiE = new gnu.chu.camposdb.proPanel();
        cLabel2 = new gnu.chu.controles.CLabel();
        tla_codiE = new gnu.chu.controles.CLinkBox();
        jtProd = new gnu.chu.controles.Cgrid(4);
        jtCli = new gnu.chu.controles.Cgrid(3);
        jtPed = new gnu.chu.controles.Cgrid(10);

        Bimpri.setToolTipText("Imprimir Relacion Productos en Pedidos");
        Bimpri.setMaximumSize(new java.awt.Dimension(24, 14));
        Bimpri.setMinimumSize(new java.awt.Dimension(24, 14));
        Bimpri.setPreferredSize(new java.awt.Dimension(24, 14));

        Pprinc.setLayout(new java.awt.GridBagLayout());

        Pcondi.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Pcondi.setMaximumSize(new java.awt.Dimension(655, 79));
        Pcondi.setMinimumSize(new java.awt.Dimension(655, 79));
        Pcondi.setName(""); // NOI18N
        Pcondi.setPreferredSize(new java.awt.Dimension(655, 79));
        Pcondi.setLayout(null);

        cLabel4.setText("Emp");
        Pcondi.add(cLabel4);
        cLabel4.setBounds(630, 0, 30, 18);

        emp_codiE.setPreferredSize(new java.awt.Dimension(39, 18));
        Pcondi.add(emp_codiE);
        emp_codiE.setBounds(660, 0, 40, 18);

        cLabel5.setText("Delegación");
        Pcondi.add(cLabel5);
        cLabel5.setBounds(280, 0, 70, 18);
        Pcondi.add(sbe_codiE);
        sbe_codiE.setBounds(350, 0, 37, 18);

        cLabel6.setText("De Fecha");
        Pcondi.add(cLabel6);
        cLabel6.setBounds(2, 0, 60, 18);
        Pcondi.add(pvc_feciniE);
        pvc_feciniE.setBounds(60, 0, 76, 18);

        cLabel7.setText("A Fecha");
        Pcondi.add(cLabel7);
        cLabel7.setBounds(140, 0, 44, 18);
        Pcondi.add(pvc_fecfinE);
        pvc_fecfinE.setBounds(190, 0, 75, 18);

        cLabel16.setText("Alm");
        cLabel16.setPreferredSize(new java.awt.Dimension(60, 18));
        Pcondi.add(cLabel16);
        cLabel16.setBounds(390, 0, 30, 18);

        rep_codiE.setAncTexto(30);
        rep_codiE.setPreferredSize(new java.awt.Dimension(152, 18));
        Pcondi.add(rep_codiE);
        rep_codiE.setBounds(60, 20, 190, 18);

        zon_codiE.setAncTexto(30);
        zon_codiE.setPreferredSize(new java.awt.Dimension(152, 18));
        Pcondi.add(zon_codiE);
        zon_codiE.setBounds(290, 20, 200, 18);

        cLabel18.setText("Zona");
        cLabel18.setPreferredSize(new java.awt.Dimension(60, 18));
        Pcondi.add(cLabel18);
        cLabel18.setBounds(250, 20, 40, 18);

        Baceptar.setText("Aceptar");
        Pcondi.add(Baceptar);
        Baceptar.setBounds(610, 38, 100, 30);

        cLabel21.setText("Repres.");
        cLabel21.setPreferredSize(new java.awt.Dimension(60, 18));
        Pcondi.add(cLabel21);
        cLabel21.setBounds(2, 20, 60, 18);

        alm_codiE.setAncTexto(25);
        Pcondi.add(alm_codiE);
        alm_codiE.setBounds(420, 0, 200, 17);

        cLabel22.setText("Ruta");
        cLabel22.setPreferredSize(new java.awt.Dimension(60, 18));
        Pcondi.add(cLabel22);
        cLabel22.setBounds(490, 20, 30, 18);

        rut_codiE.setAncTexto(30);
        rut_codiE.setPreferredSize(new java.awt.Dimension(152, 18));
        Pcondi.add(rut_codiE);
        rut_codiE.setBounds(520, 20, 200, 18);

        cLabel1.setText("Tipo Listado");
        Pcondi.add(cLabel1);
        cLabel1.setBounds(340, 40, 80, 18);
        Pcondi.add(pro_codiE);
        pro_codiE.setBounds(60, 40, 271, 18);

        cLabel2.setText("Producto");
        Pcondi.add(cLabel2);
        cLabel2.setBounds(2, 40, 50, 18);

        tla_codiE.setAncTexto(30);
        tla_codiE.setPreferredSize(new java.awt.Dimension(200, 17));
        Pcondi.add(tla_codiE);
        tla_codiE.setBounds(420, 40, 180, 18);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        Pprinc.add(Pcondi, gridBagConstraints);

        jtProd.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jtProd.setMaximumSize(new java.awt.Dimension(333, 345));
        jtProd.setMinimumSize(new java.awt.Dimension(333, 345));
        jtProd.setPreferredSize(new java.awt.Dimension(333, 345));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        Pprinc.add(jtProd, gridBagConstraints);

        jtCli.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jtCli.setMaximumSize(new java.awt.Dimension(322, 208));
        jtCli.setMinimumSize(new java.awt.Dimension(322, 208));
        jtCli.setPreferredSize(new java.awt.Dimension(322, 208));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        Pprinc.add(jtCli, gridBagConstraints);

        jtPed.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jtPed.setMaximumSize(new java.awt.Dimension(324, 131));
        jtPed.setMinimumSize(new java.awt.Dimension(324, 131));
        jtPed.setPreferredSize(new java.awt.Dimension(324, 131));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        Pprinc.add(jtPed, gridBagConstraints);

        getContentPane().add(Pprinc, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gnu.chu.controles.CButton Baceptar;
    private gnu.chu.controles.CButton Bimpri;
    private gnu.chu.controles.CPanel Pcondi;
    private gnu.chu.controles.CPanel Pprinc;
    private gnu.chu.controles.CLinkBox alm_codiE;
    private gnu.chu.controles.CLabel cLabel1;
    private gnu.chu.controles.CLabel cLabel16;
    private gnu.chu.controles.CLabel cLabel18;
    private gnu.chu.controles.CLabel cLabel2;
    private gnu.chu.controles.CLabel cLabel21;
    private gnu.chu.controles.CLabel cLabel22;
    private gnu.chu.controles.CLabel cLabel4;
    private gnu.chu.controles.CLabel cLabel5;
    private gnu.chu.controles.CLabel cLabel6;
    private gnu.chu.controles.CLabel cLabel7;
    private gnu.chu.camposdb.empPanel emp_codiE;
    private gnu.chu.controles.Cgrid jtCli;
    private gnu.chu.controles.Cgrid jtPed;
    private gnu.chu.controles.Cgrid jtProd;
    private gnu.chu.camposdb.proPanel pro_codiE;
    private gnu.chu.controles.CTextField pvc_fecfinE;
    private gnu.chu.controles.CTextField pvc_feciniE;
    private gnu.chu.controles.CLinkBox rep_codiE;
    private gnu.chu.controles.CLinkBox rut_codiE;
    private gnu.chu.camposdb.sbePanel sbe_codiE;
    private gnu.chu.controles.CLinkBox tla_codiE;
    private gnu.chu.controles.CLinkBox zon_codiE;
    // End of variables declaration//GEN-END:variables
}
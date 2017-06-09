package gnu.chu.anjelica.pad;
/**
 *
 * <p>Titulo: MantTarifa </p>
 * <p>Descripción: Mantenimiento Tarifas de Ventas</p>
 * <p>Copyright: Copyright (c) 2005-2017
 *  Este programa es software libre. Puede redistribuirlo y/o modificarlo bajo
 *  los terminos de la Licencia Pública General de GNU según es publicada por
 *  la Free Software Foundation, bien de la versión 2 de dicha Licencia
 *  o bien (según su elección) de cualquier versión posterior.
 *  Este programa se distribuye con la esperanza de que sea útil,
 *  pero SIN NINGUNA GARANTIA, incluso sin la garantía MERCANTIL implícita
 *  o sin garantizar la CONVENIENCIA PARA UN PROPOSITO PARTICULAR.
 *  Véase la Licencia Pública General de GNU para más detalles.
 *  Debería haber recibidof una copia de la Licencia Pública General junto con este programa.
 *  Si no ha sido así, escriba a la Free Software Foundation, Inc.,
 *  en 675 Mass Ave, Cambridge, MA 02139, EEUU.
 * </p>
 * @author chuchiP
 *
 */ 
import gnu.chu.Menu.Principal;
import gnu.chu.anjelica.listados.Listados;
import gnu.chu.controles.StatusBar;
import gnu.chu.interfaces.PAD;
import gnu.chu.sql.DatosTabla;
import gnu.chu.utilidades.EntornoUsuario;
import gnu.chu.utilidades.Formatear;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.mensajes;
import gnu.chu.utilidades.navegador;
import gnu.chu.utilidades.ventanaPad;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;


public class MantTarifa extends ventanaPad implements PAD, JRDataSource
{
  double tarIncpre;
  String localeEmpresa;
  boolean swLocaleEmpresa;
  String s;
  String fecini,tipo;
  boolean ARG_MODCONSULTA=false;
  boolean swInicio=false;
  boolean swVerArtic=false;
  final static int JT_COMREP=4;
  
  public MantTarifa(EntornoUsuario eu, Principal p)
  {
    this(eu,p,null);
  }

  public MantTarifa(EntornoUsuario eu, Principal p, Hashtable ht)
  {
      EU = eu;
      vl = p.panel1;
      jf = p;
      eje = true;

      setTitulo("Mant. Tarifas de Productos");

      try
      {
        if (ht != null)
        {
          if (ht.get("modoConsulta") != null)
            ARG_MODCONSULTA = Boolean.parseBoolean(ht.get("modoConsulta").toString());
        }

        if (jf.gestor.apuntar(this))
          jbInit();
        else
          setErrorInit(true);
      }
      catch (Exception e)
      {
        ErrorInit(e);
      }
    }

    public MantTarifa(gnu.chu.anjelica.menu p, EntornoUsuario eu)
    {

      EU = eu;
      vl = p.getLayeredPane();
      setTitulo("Mant. Tarifas de Productos");
      eje = false;

      try
      {
        jbInit();
      }
      catch (Exception e)
      {
        ErrorInit(e);
      }
    }

    private void jbInit() throws Exception
    { 
      this.setVersion("2017-05-22" + (ARG_MODCONSULTA ? " SOLO LECTURA" : ""));
      statusBar = new StatusBar(this);
      nav = new navegador(this,dtCons,false);
      iniciarFrame();
      strSql = "SELECT tar_fecini,tar_fecfin,tar_codi FROM tarifa"+
          " group by tar_fecini,tar_fecfin,tar_codi" +
          " order by tar_fecini,tar_codi";
      this.getContentPane().add(nav, BorderLayout.NORTH);
      this.getContentPane().add(statusBar, BorderLayout.SOUTH);
      this.setPad(this);
      navActivarAll();
      dtCons.setLanzaDBCambio(false);
      initComponents();
      iniciarBotones(Baceptar, Bcancelar);
      this.setSize(new Dimension(582,522));
      conecta();
      activar(false);
      if (ARG_MODCONSULTA)
      {
        nav.removeBoton(navegador.ADDNEW);
        nav.removeBoton(navegador.EDIT);
        nav.removeBoton(navegador.DELETE);
      }
//      statusBar.add(Bimpri, new GridBagConstraints(9, 0, 1, 2, 0.0, 0.0
//                            , GridBagConstraints.EAST,
//                            GridBagConstraints.VERTICAL,
//                            new Insets(0, 5, 0, 0), 0, 0));

    }
    @Override
    public void iniciarVentana() throws Exception
    {
      pro_codiE.iniciar(dtStat, this, vl, EU);
      pro_codiE.setUsaCodigoVenta(true);
      pro_codiE.setColumnaAlias("pro_codart");
      tar_feciniE.setColumnaAlias("tar_fecini");
      tar_fecfinE.setColumnaAlias("tar_fecfin");
      tar_codiE.setColumnaAlias("tar_codi");
      localeEmpresa=MantPaises.getLocalePais(pdempresa.getPais(dtStat, EU.em_cod),dtStat);
      activarEventos();
      activar(false);
//      verDatos();p
      nav.requestFocus();
      Pprinc.setDefButton(Baceptar);
//      Pprinc.setEscButton(Bcancelar);
    }

    void activarEventos()
    {
        BTexto.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                BTexto_actionPerformed();
            }
         });
        
        Bimpri.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                Bimpri_actionPerformed();
            }
         });

      tar_feciniE.addFocusListener(new FocusAdapter()
      {
            @Override
        public void focusLost(FocusEvent e)
        {
          if (tar_feciniE.isQuery()|| tar_feciniE.getError())
            return;
          try {
            if (!tar_feciniE.isNull())
            {
              tar_nusemE.setValorDec(actFecha(tar_feciniE.getDate()));
              tar_fecfinE.setText( Formatear.sumaDias(tar_feciniE.getText(),"dd-MM-yyyy",6));
            }
          } catch (Exception k)
          {
            Error("Error al Comprobar Fecha",k);
          }
        }
      });

      Bocul.addFocusListener(new FocusAdapter()
      {
        @Override
        public void focusGained(FocusEvent e)
        {
          irGrid();
        }
      });
    }

    int actFecha(java.util.Date fecha)
    {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(fecha);
      return gc.get(GregorianCalendar.WEEK_OF_YEAR);
    }
    
    void BTexto_actionPerformed()
    {
         String s=mensajes.mensajeExplica("Copie y pegue", "Codigo:Precio","");
         if (s==null)
             return;
         int nLen=s.length();
         int modo=0; // Buscando codigo producto
         int inicProd,finProd;
         int codProd=0;
         double precio;
              
        char sep=new DecimalFormatSymbols(new Locale("es","","")).getDecimalSeparator();
        NumberFormat nf = NumberFormat.getInstance(new Locale("es","",""));
         for (int n=0;n<nLen;n++)
         {
            if (modo==0)
            {              
              if (Character.isDigit(s.charAt(n)) )
              {
                  inicProd=n;
                  finProd=0;
                  for (;n<nLen;n++)
                  {
//                      if (!Character.isAlphabetic(s.charAt(n)))
//                          break;
                      if (s.charAt(n)==':')
                      {
                          finProd=n;
                          break;
                      }
                  }
                  if (finProd>0)
                  {
                      codProd=Integer.valueOf(s.substring(inicProd,finProd).trim());
                      modo=1; // Buscando precio
                      continue;
                  }
              }
            }
            if (modo==1)
            { // Buscando precio
                 if (!Character.isDigit(s.charAt(n)) )
                 {
                     modo=0;
                     continue;
                 }
//                if (!Character.isAlphabetic(s.charAt(n)))
//                { 
//                    modo=0;
//                    continue;
//                }
            
                  inicProd=n;
                  finProd=0;
                  for (;n<nLen;n++)
                  {
//                      if (!Character.isAlphabetic(s.charAt(n)))
//                          break;
                     if (!Character.isDigit(s.charAt(n)) && s.charAt(n)!=sep )
                      {
                          finProd=n;
                          break;
                      }
                  }
                  if (finProd>0 && finProd>inicProd)
                  {                      
                      try                   
                      {
                          precio=nf.parse(s.substring(inicProd,finProd).trim()).doubleValue();
                      } catch (ParseException ex)
                      {
                          modo=0;
                          continue;
                      }
                      pro_codartE.setText(""+codProd);
                      pro_codartE.pro_codiE_focusLost();
                      ArrayList v=new ArrayList();
                      v.add(pro_codartE.getText());
                      v.add(pro_codartE.getTextNomb());
                      v.add(precio);
                      v.add("Importado");
                      v.add(0);
                      jt.addLinea(v);
                  }
                  modo=0; // Buscando codigo
            }
            
         }
         
         
    }
    void Bimpri_actionPerformed()
   {
     if (dtCons.getNOREG())
       return;
     try {
       if (dtCons.getNOREG())
       {
         msgBox("NO HAY NINGUN PRODUCTO SELECIONADO");
         return;
       }

       HashMap mp = Listados.getHashMapDefault();
       swLocaleEmpresa=loc_codiE.getValor().equals(localeEmpresa);
       swInicio=true;
       Listados lis=  Listados.getListado(EU.em_cod, Listados.TARIFA, dtStat);
       mp.put("tar_feciniP", tar_feciniE.getDate());
       mp.put("tar_fecfinP", tar_fecfinE.getDate());
       mp.put("impRefer", opImpRef.isSelected());
       mp.put("tar_nombP", tar_impriE.getText()); 
       mp.put("logo", lis.getPathLogo()); 
       JasperReport jr;
       jr = Listados.getJasperReport(EU,lis.getNombFich());
       s="select tar_codori,tar_incpre from tipotari where tar_codi="+tar_impriE.getValor();
       if (!dtStat.select(s))
       {
           msgBox("Tarifa no encontrada");
           return;
       }
       int tarCodi=tar_impriE.getValorInt();
       tarIncpre=0;
       if (dtStat.getInt("tar_codori")!=0)
       {
           tarCodi=dtStat.getInt("tar_codori");
           tarIncpre=dtStat.getDouble("tar_incpre",true);
       }
       s="select * from tarifa  "+
            " where tar_codi = "+tarCodi+
            " and tar_fecini = TO_DATE('"+tar_feciniE.getText()+"','dd-MM-yyyy') "+
            " order by tar_linea";
       
       dtCon1.select(s);

       JasperPrint jp = JasperFillManager.fillReport(jr, mp, this);
       gnu.chu.print.util.printJasper(jp, EU);
     }
     catch (JRException | ParseException | SQLException | PrinterException k)
     {
       Error("Error al imprimir Tarifa", k);
     }
   }
    void irGrid()
    {
      if (nav.pulsado!=navegador.ADDNEW && nav.pulsado!=navegador.EDIT)
        return;
      
      if (tar_feciniE.isNull() || tar_fecfinE.isNull())
      {
        tar_feciniE.requestFocus();
        return;
      }
      if (! tar_codiE.controla())
      {
          mensajeErr("Tipo de Tarifa NO valida");
          tar_codiE.requestFocus();
          return;
      }
      try {
        if (nav.pulsado == navegador.ADDNEW )
        {
          s = "SELECT * FROM tarifa WHERE "+
              " tar_fecini = TO_DATE('"+tar_feciniE.getText()+"','dd-MM-yyyy') "+
              " and tar_codi = " + tar_codiE.getValorInt();
          if (dtCon1.select(s))
          {
            int ret=mensajes.mensajeYesNo("Le ha dado a Tarifa nueva, pero ya existe una tarifa con estos criterios. Desea editarla");
            if (ret!=mensajes.YES)
            {
                msgBox("Alta cancelada");
                mensaje("");
                activaTodo();
                nav.pulsado=navegador.NINGUNO;
                return;
            }
            verDatLin(tar_feciniE.getText(), tar_codiE.getText(),tar_fecfinE.getText(),0);
            jt.cargaTodo();
            fecini=tar_feciniE.getText();
            tipo=tar_codiE.getText();
            nav.pulsado = navegador.EDIT;
            mensaje("Editando ... ");
          }
        }

        Pcabe.setEnabled(false);
        Baceptar.setEnabled(true);
        if (nav.pulsado==navegador.ADDNEW &&  !tar_fecopE.isNull())
        {// Copiar los datos de la anterior TARIFA
          s = "SELECT * FROM tarifa WHERE " +
              " tar_fecini = TO_DATE('" + tar_fecopE.getText() +"','dd-MM-yyyy') " +
              " and tar_codi = " + tar_copiaE.getValor();
          if (dtCon1.select(s))
          {
            verDatLin(tar_fecopE.getText(), tar_copiaE.getValor(),
                      tar_fecfinE.getText(),tar_incremE.getValorDec());
            jt.cargaTodo();
          }
          else
          {
              msgBox("No encontrada tarifa de origen. Recuerde que la fecha debe ser la de inicio");
          }
        }
        jt.setEnabled(true);
        jt.requestFocusInicio();
        pro_codartE.resetCambio();
      } catch (Exception k)
      {
        Error("ERROR al ir al Grid",k);
      }
    }

    void guardaDatos(String fecha,String tipo)
    {
      try {

        borDatos(fecha,tipo);

        int nRow = jt.getRowCount();
        int grupo=0;
        int animal=0;
        dtAdd.addNew("tarifa");
        for (int n = 0; n < nRow; n++)
        {
          if ( jt.getValString(n,0).trim().equals(""))
          {
              if (jt.getValString(n,1).trim().equals(""))
                  continue;
              else
                  grupo++;
          }
          if ( jt.getValString(n,0).trim().equals("X") && !jt.getValString(n,1).trim().equals(""))
              animal++;
//          if (jt.getValorDec(n,2)==0)
//            continue;
          dtAdd.addNew();
          dtAdd.setDato("tar_fecini",tar_feciniE.getText(),"dd-MM-yyyy");
          dtAdd.setDato("tar_fecfin",tar_fecfinE.getText(),"dd-MM-yyyy");
          dtAdd.setDato("tar_linea",n);
          dtAdd.setDato("tar_codi",tar_codiE.getValorInt());
          dtAdd.setDato("pro_codart",jt.getValString(n,0));
          dtAdd.setDato("pro_nomb",jt.getValString(n,1));
          dtAdd.setDato("tar_preci",jt.getValorDec(n,2));
          dtAdd.setDato("tar_comen",jt.getValString(n,3));
          dtAdd.setDato("tar_comrep",jt.getValorDec(n,JT_COMREP));
          dtAdd.setDato("tar_grupo",grupo);
          dtAdd.setDato("tar_tipo",animal);
          dtAdd.update(stUp);
        }
        ctUp.commit();
        mensajeErr("Datos ... Guardados");
      } catch (Exception k)
      {
        Error("Error en La insercion de Referencias",k);
        return;
      }
    }

    void  borDatos(String fecha,String tipo) throws SQLException,java.text.ParseException
    {
      s = "DELETE FROM tarifa " +
          " WHERE tar_fecini = TO_DATE('" + fecha + "','dd-MM-yyyy') " +
          " AND tar_codi = " + tipo  ;
      stUp.executeUpdate(dtAdd.parseaSql(s));
    }
    
    void verDatos()
    {
      try
      {
        if (dtCons.getNOREG())
          return;
        tar_feciniE.setText(dtCons.getFecha("tar_fecini","dd-MM-yyyy"));
        tar_fecfinE.setText(dtCons.getFecha("tar_fecfin","dd-MM-yyyy"));

        tar_codiE.setText(dtCons.getString("tar_codi"));
        tar_impriE.setValor(dtCons.getString("tar_codi"));
        verDatLin(tar_feciniE.getText(),tar_codiE.getText(),tar_fecfinE.getText(),0);
      } catch (Exception k)
      {
        Error("Error al ver datos",k);
      }
    }
    void verDatosArtic(String artic)
    {
        try
        {    
           
         if (! swVerArtic)
             return;
         String s="select pro_codi,pro_nomb from v_articulo where pro_codart = '"+artic+"'";
         dtCon1.select(s);
         jtArt.setDatos(dtCon1);
        } catch (SQLException k)
        {
            Error("Error al ver codigos de articulos de venta",k);
        }
    }
    void verDatLin(String fecha,String tipo,String fecfin,double increm) throws Exception
    {
      s = "SELECT pro_codart,pro_nomb,tar_preci,tar_comen,tar_comrep " +
          " FROM tarifa " +
          " WHERE tar_fecini = TO_DATE('"+fecha+"','dd-MM-yyyy') "+
          " AND tar_codi = "+tipo+
          (pro_codiE.isNull()?"":" and pro_codart like '%"+pro_codiE.getText()+"%'")+
          " order by tar_linea";
      if (jt.isEnabled())
        jt.setEnabled(false);
      swVerArtic=false;
      if (! dtCon1.select(s))
      {
        mensajeErr("Registro BORRADO");
        tar_fecfinE.resetTexto();
        tar_nusemE.resetTexto();
        jt.removeAllDatos();
        return;
      }
      tar_fecfinE.setText(fecfin);
      tar_nusemE.setValorDec(actFecha(tar_feciniE.getDate()));

      jt.setDatos(dtCon1);
      if (increm>0)
      { // Sumarle el incremento.
          int nl=jt.getRowCount();
          for (int n=0;n<nl;n++)
          {
              if (! jt.getValString(n,0).equals("") && !jt.getValString(n,0).equals("X") )
              {
                  jt.setValor(jt.getValorDec(n,2)==0?0: Formatear.redondea(jt.getValorDec(n,2)+increm,2),n,2);
              }
          }
      }
      jt.requestFocusInicio();
      swVerArtic=true;
      verDatosArtic(jt.getValString(0,0));
      
    }
    @Override
    public void activar(boolean act)
    {
      activar(navegador.TODOS,act);
    }
    void activar(int modo,boolean act)
    {
      if (modo==navegador.TODOS)
        jt.setEnabled(act);
      pro_codiE.setEnabled(false);
      tar_fechaE.setEnabled(false);
      Baceptar.setEnabled(act);
      Bcancelar.setEnabled(act);
      Pcabe.setEnabled(act);
    }
   @Override
    public void PADPrimero()
    {
      verDatos();
    }
    @Override
    public void PADAnterior()
    {
      verDatos();
    }
    
    @Override
    public void PADSiguiente()
    {
      verDatos();
    }

    @Override
    public void PADUltimo()
    {
      verDatos();
    }
    @Override
    public void PADQuery() {
        activar(navegador.QUERY, true);
        Pcabe.setQuery(true);
        pro_codiE.setEnabled(true);
        tar_fechaE.setEnabled(true);
        tar_fechaE.setQuery(false);
        Pcabe.resetTexto();
        
        tar_feciniE.requestFocus();

    }

    @Override
  public void ej_query1()
  {
     
    if (Pcabe.getErrorConf()!=null)
    {
        msgBox("Errores en condiciones de busqueda");
        return;
    }
    Baceptar.setEnabled(false);
    ArrayList v=new ArrayList();
    v.add(tar_feciniE.getStrQuery());
    v.add(tar_fecfinE.getStrQuery());
    v.add(tar_codiE.getStrQuery());
    v.add(pro_codiE.getStrQuery());
    Pcabe.setQuery(false);
    s="SELECT tar_fecini,tar_fecfin,tar_codi FROM tarifa";
    if (!tar_fechaE.isNull())
            s+=" where tar_fecini <= to_date('"+tar_fechaE.getText()+"','dd-MM-yyyy')"+
           " and tar_fecfin >= to_date('"+tar_fechaE.getText()+"','dd-MM-yyyy')";
    else
        s=creaWhere(s,v,tar_fechaE.isNull());
    
    s+=" group by tar_fecini,tar_fecfin,tar_codi"+
        " order by tar_fecini,tar_codi";
//    debug(s);
    try
    {
      if (!dtCons.select(s))
      {
        mensaje("");
        mensajeErr("No encontrados Registros para estos criterios");
        rgSelect();
        activaTodo();
        return;
      }
      mensaje("");
      strSql = s;
      activaTodo();
      rgSelect();
//      verDatos();
      mensajeErr("Nuevos registros selecionados");
    }
    catch (Exception ex)
    {
      fatalError("Error al buscar Tarifas: ", ex);
    }
  }

  @Override
  public void canc_query() {
    Pcabe.setQuery(false);
    pro_codiE.resetTexto();
    mensaje("");
    mensajeErr("Consulta ... CANCELADA");
    activaTodo();
    verDatos();
  }

    @Override
  public void PADEdit() {
    
    mensaje("Editando ....");
//    ejeNume=eje_numeE.getValorInt();
    if (!pro_codiE.isNull())
    {
        pro_codiE.resetTexto();
        verDatos();
    }
    jt.cargaTodo();
    fecini=tar_feciniE.getText();
    tipo=tar_codiE.getText();
    activar(true);
    tar_fecopE.setEnabled(false);
    jt.requestFocusInicioLater();
  }
  @Override
  public void ej_edit1() {
      jt.salirGrid();

    if (cambiaLineaJT()>=0)
    {
      jt.requestFocusSelected();
      return;
    }
    int row=checkRepetido();
    if (row>=0)
    {
        jt.requestFocus(row,0);
        return;
    }
    guardaDatos(fecini,tipo);
    activaTodo();
    verDatos();
    mensaje("");
  }
  @Override
  public void canc_edit() {
    activaTodo();
    mensajeErr("Modificacion de Datos ... Cancelada");
    verDatos();
    mensaje("");
  }
    @Override
  public void PADAddNew() {

    Pcabe.resetTexto();
    jt.removeAllDatos();
    activar(navegador.QUERY, true);
    Baceptar.setEnabled(false);
    tar_fecopE.setEnabled(true);
//    eje_numeE.setValorDec(EU.ejercicio);
    tar_feciniE.requestFocus();
    mensaje("Insertando ....");
  }

  public void ej_addnew1() {
    jt.procesaAllFoco();

    if (cambiaLineaJT()>=0)
    {
      jt.requestFocusSelected();
      return;
    }
    int row=checkRepetido();
    if (row>=0)
    {
        jt.requestFocus(row,0);
        return;
    }
    guardaDatos(tar_feciniE.getText(),tar_codiE.getText());
    activaTodo();
    verDatos();
    mensaje("");
  }
  int checkRepetido()
  {
      int nRow=jt.getRowCount();
      for (int n=0;n<nRow-1;n++)
      {
          if (jt.getValString(n,0).equals("") || jt.getValString(n,0).equals("X"))
                  continue;
          for (int n1=n+1;n1<nRow;n1++)
          {
             
              if (jt.getValString(n,0).equals(jt.getValString(n1,0)))
              {
                  mensajeErr("Producto: "+jt.getValString(n,0)+" Ya existe en linea "+n);
                  return n1;
              }
          }
      }
      return -1;
  }
  @Override
  public void canc_addnew() {
    activaTodo();
    mensajeErr("Insercion de Datos ... Cancelada");
    verDatos();
    mensaje("");
  }
    @Override
  public void PADDelete() {
    Baceptar.setEnabled(true);
    Bcancelar.setEnabled(true);
    Bcancelar.requestFocus();
    mensaje("Borrando ....");
  }
  @Override
  public void ej_delete1() {
    try
    {
      borDatos(tar_feciniE.getText(), tar_codiE.getText());
      ctUp.commit();
      rgSelect();
    } catch (Exception k)
    {
      Error("Error al borrar datos",k);
    }
    activaTodo();
   
    verDatos();
    mensaje("");
    mensajeErr("Datos .... Borrados");

  }
  public void canc_delete() {
    activaTodo();
    verDatos();
    mensaje("");
    mensajeErr("Borrado de Registro ... ANULADO");
  }
  
 
  int cambiaLineaJT()
  {
    if (pro_codartE.isNull() || pro_codartE.getText().equals("X"))
      return -1; // No hay producto o es de tipo... paso
    try {

     
      if (!pro_codartE.controla(false))
      {
        mensajeErr(pro_codartE.getMsgError());
        return 0;
      }
//      if (tar_preciE.getValorDec() == 0)
//      {
//        mensajeErr("Introduzca un precio de Tarifa");
//        return 2    ;
//      }
    } catch (Exception k)
    {
      Error("ERROR AL controlar Linea del Grid",k);
      return 0;
    }
    return -1;
  }
    @Override
  public void rgSelect() throws SQLException
  {
    super.rgSelect();
    if (! dtCons.getNOREG())
    {
      dtCons.last();
      nav.setEnabled(navegador.ULTIMO,false);
      nav.setEnabled(navegador.SIGUIENTE,false);
    }
    verDatos();
  }
  
  @Override
  public void afterConecta() throws SQLException, java.text.ParseException
  {
    s = "SELECT tar_codi,tar_nomb FROM tipotari " +
        " ORDER BY tar_codi ";
    if (dtStat.select(s))
    {
      tar_codiE.addDatos(dtStat, true);
      dtStat.first();
      do
      {
        tar_copiaE.addItem(dtStat.getString("tar_nomb"),
                          dtStat.getString("tar_codi"));
      }
      while (dtStat.next());
    }
    pro_codartE.iniciar(dtStat, this, vl, EU);
    tar_impriE.setDatos(pdtipotar.getTiposTarifa(dtCon1,-1));
    loc_codiE.setDatos(MantIdiomas.getDatos(dtAdd));
    loc_codiE.setValor(MantPaises.getLocalePais(pdempresa.getPais(dtStat, EU.em_cod), dtCon1));
    pro_codartE.setProNomb(null);
  }
  public static double getPrecTar(DatosTabla dt,int proCodi,int cliCodi, int tarCodi,java.util.Date fecAlb) throws SQLException
  {
      return getPrecTar(dt,proCodi,cliCodi, tarCodi, Formatear.getFechaVer(fecAlb));
  }
  
  /**
   * Devuelve el precio de tarifa para un producto y/o cliente en una fecha dada
   * @param dt
   * @param proCodi Articulo
   * @param cliCodi Cliente (0) Si es tarifa generica
   * @param tarCodi Tarifa
   * @param fecAlb Fecha Albaran
   * @return 0 si no encuentra la tarifa
   * @throws SQLException 
   */
 public static double getPrecTar(DatosTabla dt,int proCodi,int cliCodi, int tarCodi,String fecAlb) throws SQLException
 {
   String s;
   if (fecAlb.trim().equals(""))
       return 0;
   String codVenta=MantArticulos.getCodigoVenta(proCodi,dt);
  
   if (codVenta==null)
       return 0;
    
   String codVentaOri=codVenta;
   boolean isCodCorte=false;
   double proCointa=dt.getDouble("pro_cointa",true);
   if (codVenta.endsWith("/"))
   { // Es producto corte. Busco padre
         codVentaOri=codVenta.substring(0,codVenta.length()-1);
         isCodCorte=true;
   }
   if (cliCodi!=0)
   { // Busco tarifa especifica para cliente
       s = " SELECT tar_preci,tar_fecini " +
         " FROM taricli as t "+
         " where pro_codart= '"+codVenta+"'"+
         " and cli_codi = " + cliCodi +
         " AND tar_fecini <=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " AND (tar_fecfin >=  TO_DATE('" + fecAlb + "','dd-MM-yyyy') or tar_fecfin is null) "+
         " order by tar_fecini";
       if (dt.select(s))
        return dt.getDouble("tar_preci", true);  
       if (isCodCorte)
       { // Busco el codigo original
           s = " SELECT tar_preci,tar_fecini " +
             " FROM taricli as t "+
             " where pro_codart= '"+codVentaOri+"'"+
             " and cli_codi = " + cliCodi +
             " AND tar_fecini <=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
             " AND (tar_fecfin >=  TO_DATE('" + fecAlb + "','dd-MM-yyyy') or tar_fecfin is null) "+
             " order by tar_fecini";
            if (dt.select(s))
             return dt.getDouble("tar_preci", true)+ proCointa;  
       }
   }
   /** 
    * Busco precio para el producto en tarifa mandada
    */
    s = " SELECT tar_preci,tar_fecini " +
         " FROM tarifa as t "+
         " where pro_codart= '"+codVenta+"'"+
         " and tar_codi = " + tarCodi +
         " AND tar_fecini <=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " AND tar_fecfin >=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " order by tar_fecini";
    if (dt.select(s))
      return dt.getDouble("tar_preci", true);  
    if (isCodCorte)
    { // Es producto corte. Busco padre
         s = " SELECT tar_preci,tar_fecini   " +
         " FROM tarifa as t "+
         " where pro_codart= '"+codVentaOri+"'"+
         " and tar_codi = " +  tarCodi +
         " AND tar_fecini <=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " AND tar_fecfin >=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " order by tar_fecini";
         if (dt.select(s))
          return dt.getDouble("tar_preci", true)+ proCointa;  
    }
    /**
     * Busco precio en tarifa padre (si la hay)
     */
   s= " SELECT * FROM tipotari WHERE tar_codi = " + tarCodi+
       " and tar_codori > 0";
   if (dt.select(s))
   {
     
     double tarIncPre = dt.getDouble("tar_incpre");
     int tarCodOri= dt.getInt("tar_codori");
     s = " SELECT tar_preci,tar_fecini " +
         " FROM tarifa as t " +
         " where pro_codart= '"+codVenta+"'"+
         " and tar_codi = " +  tarCodOri +
         " AND tar_fecini <=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " AND tar_fecfin >=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " order by tar_fecini";
     if (dt.select(s))
       return dt.getDouble("tar_preci", true)==0?0:dt.getDouble("tar_preci", true) +
           tarIncPre;
     if (isCodCorte)
     {
          s = " SELECT tar_preci,tar_fecini  " +
         " FROM tarifa as t  where pro_codart = '" + codVentaOri +"'"+
         " and tar_codi = " + tarCodOri +
         " AND tar_fecini <=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " AND tar_fecfin >=  TO_DATE('" + fecAlb + "','dd-MM-yyyy')" +
         " order by tar_fecini";
         if (dt.select(s))
             return dt.getDouble("tar_preci", true)==0?0:dt.getDouble("tar_preci", true) +
                 proCointa+
                 tarIncPre; 
     }
   }
   return 0;
 }
 /**
  * Busca si una tarifa es de Costo.
  * @param dt DatosTabla a utilizar
  * @param tarCodi Codigo de Tarifa
  * @return true si es una tarifa de costo, false si no encuentra la tarifa o no es de costo.
  * @throws java.sql.SQLException
  */
 public static boolean isTarifaCosto(DatosTabla dt, int tarCodi) throws SQLException
 {
      String s = " SELECT tar_tipo FROM tipotari WHERE tar_codi = " + tarCodi;
      if (!dt.select(s))
          return false;
      return dt.getString("tar_tipo").equals("C");
 }
 /**
  * Devuelve el nombre de una tarifa
  * @param dt datosTabla para acceder a la DB
  * @param tarCodi codigo de tarifa
  * @return Nombre de tarifa. Null SI No existe
  * @throws SQLException Error al acceder a la DB
  */
 public static String getTarNomb(DatosTabla dt, int tarCodi) throws SQLException
 {
     String s= "SELECT tar_nomb FROM tipotari WHERE tar_codi = " +tarCodi;
     if (!dt.select(s))
         return null;
     return dt.getString("tar_nomb");
 }
 
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        pro_nombE = new gnu.chu.controles.CTextField();
        tar_comenG = new gnu.chu.controles.CTextField(Types.CHAR,"X",150);
        tar_preciE = new gnu.chu.controles.CTextField(Types.DECIMAL,"###9.99");
        pro_codartE = new gnu.chu.camposdb.proPanel();
        tar_comrepE = new gnu.chu.controles.CTextField(Types.DECIMAL,"#9.99");
        Pprinc = new gnu.chu.controles.CPanel();
        Pcabe = new gnu.chu.controles.CPanel();
        cLabel5 = new gnu.chu.controles.CLabel();
        tar_feciniE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        cLabel6 = new gnu.chu.controles.CLabel();
        tar_fecfinE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        cLabel1 = new gnu.chu.controles.CLabel();
        tar_nusemE = new gnu.chu.controles.CTextField(Types.DECIMAL,"99");
        cLabel2 = new gnu.chu.controles.CLabel();
        cPanel1 = new gnu.chu.controles.CPanel();
        cLabel3 = new gnu.chu.controles.CLabel();
        tar_fecopE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        cLabel7 = new gnu.chu.controles.CLabel();
        tar_incremE = new gnu.chu.controles.CTextField(Types.DECIMAL,"--9.99");
        Bocul = new gnu.chu.controles.CButton();
        cLabel8 = new gnu.chu.controles.CLabel();
        tar_copiaE = new gnu.chu.controles.CComboBox();
        tar_codiE = new gnu.chu.controles.CLinkBox();
        cLabel10 = new gnu.chu.controles.CLabel();
        pro_codiE = new gnu.chu.camposdb.proPanel();
        cLabel11 = new gnu.chu.controles.CLabel();
        tar_fechaE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        BTexto = new gnu.chu.controles.CButton();
        jt = new gnu.chu.controles.CGridEditable(5) {
            public void cambiaColumna(int col,int colNueva, int row)
            {
                try
                {
                    if (col ==0)
                    {
                        if (! pro_codartE.hasCambio())
                        return;
                        pro_codartE.pro_codiE_focusLost();
                        String nombArt;
                        if (pro_codartE.getText().equals("X"))
                        {
                            nombArt="*TIPO*";
                        }
                        else
                        {
                            nombArt=pro_codartE.getNombArt(pro_codartE.getText());
                        }
                        if (nombArt==null)
                        jt.setValor("**PRODUCTO NO ENCONTRADO**", row, 1);
                        else
                        jt.setValor(nombArt, row, 1);
                        pro_codartE.resetCambio();
                    }
                }
                catch (Exception k)
                {
                    Error("Error al buscar Nombre Articulo", k);
                }
            }

            public void afterCambiaLinea()
            {
                pro_codartE.resetCambio();
                verDatosArtic(jt.getValString(0));
                //      tar_feciniG.setText(tar_feciniE.getText());
                //      tar_fecfinG.setText(tar_fecfinE.getText());
            }

            public void afterCambiaLineaDis(int nRow)
            {
                verDatosArtic(jt.getValString(nRow,0));
                //      tar_feciniG.setText(tar_feciniE.getText());
                //      tar_fecfinG.setText(tar_fecfinE.getText());
            }
            public int cambiaLinea(int row, int col)
            {
                return cambiaLineaJT();
            }
        };
        pro_codartE.setUsaCodigoVenta(true);
        pro_codartE.setText("");
        ArrayList cabecera = new ArrayList();
        cabecera.add("Codigo"); // 0 -- Codigo
        cabecera.add("Nombre"); //1-- Nombre
        cabecera.add("Precio"); // 2 -- Precio
        cabecera.add("Coment"); // 3 -- Comentario
        cabecera.add("Com.Rep"); // 4 -- Comision Repr.
        jt.setCabecera(cabecera);
        jt.setAnchoColumna(new int[]{86, 283, 60,150,60});
        jt.setAlinearColumna(new int[] {0, 0, 2,0,2});

        jt.setNumRegCargar(0);
        try {
            pro_codartE.setText("");
            ArrayList v = new ArrayList();
            v.add(pro_codartE.getTextField());
            v.add(pro_nombE);
            v.add(tar_preciE);
            v.add(tar_comenG);
            v.add(tar_comrepE);
            jt.setCampos(v);
        }catch (Exception k)
        {
            Error("Error al iniciar el grid",k);
        }
        jt.setFormatoCampos();
        jtArt = new gnu.chu.controles.Cgrid(2);
        Ppie = new gnu.chu.controles.CPanel();
        Baceptar = new gnu.chu.controles.CButton();
        Bcancelar = new gnu.chu.controles.CButton();
        Bimpresion = new gnu.chu.controles.CPanel();
        cLabel4 = new gnu.chu.controles.CLabel();
        tar_impriE = new gnu.chu.controles.CComboBox();
        Bimpri = new gnu.chu.controles.CButton(Iconos.getImageIcon("print"));
        opImpRef = new gnu.chu.controles.CCheckBox();
        cLabel9 = new gnu.chu.controles.CLabel();
        loc_codiE = new gnu.chu.controles.CComboBox();

        pro_codartE.setAceptaNulo(false);
        pro_codartE.setUsaCodigoVenta(true);

        Pprinc.setLayout(new java.awt.GridBagLayout());

        Pcabe.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Pcabe.setMaximumSize(new java.awt.Dimension(585, 85));
        Pcabe.setMinimumSize(new java.awt.Dimension(585, 85));
        Pcabe.setPreferredSize(new java.awt.Dimension(585, 85));
        Pcabe.setLayout(null);

        cLabel5.setText("Fecha");
        Pcabe.add(cLabel5);
        cLabel5.setBounds(10, 62, 40, 17);

        tar_feciniE.setMaximumSize(new java.awt.Dimension(10, 18));
        tar_feciniE.setMinimumSize(new java.awt.Dimension(10, 18));
        tar_feciniE.setPreferredSize(new java.awt.Dimension(10, 18));
        Pcabe.add(tar_feciniE);
        tar_feciniE.setBounds(60, 0, 62, 18);

        cLabel6.setText("A");
        Pcabe.add(cLabel6);
        cLabel6.setBounds(130, 0, 15, 18);

        tar_fecfinE.setMaximumSize(new java.awt.Dimension(10, 18));
        tar_fecfinE.setMinimumSize(new java.awt.Dimension(10, 18));
        tar_fecfinE.setPreferredSize(new java.awt.Dimension(10, 18));
        Pcabe.add(tar_fecfinE);
        tar_fecfinE.setBounds(150, 0, 62, 18);

        cLabel1.setText("Articulo");
        Pcabe.add(cLabel1);
        cLabel1.setBounds(140, 60, 50, 17);

        tar_nusemE.setEnabled(false);
        tar_nusemE.setMinimumSize(new java.awt.Dimension(10, 18));
        tar_nusemE.setPreferredSize(new java.awt.Dimension(10, 18));
        Pcabe.add(tar_nusemE);
        tar_nusemE.setBounds(270, 0, 20, 18);

        cLabel2.setText("Tarifa");
        Pcabe.add(cLabel2);
        cLabel2.setBounds(300, 0, 40, 18);

        cPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Copiar de", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        cPanel1.setLayout(null);

        cLabel3.setText("Fecha");
        cLabel3.setPreferredSize(new java.awt.Dimension(33, 18));
        cPanel1.add(cLabel3);
        cLabel3.setBounds(1, 15, 40, 17);

        tar_fecopE.setPreferredSize(new java.awt.Dimension(10, 18));
        cPanel1.add(tar_fecopE);
        tar_fecopE.setBounds(40, 15, 65, 17);

        cLabel7.setText("Increm.");
        cLabel7.setPreferredSize(new java.awt.Dimension(64, 18));
        cPanel1.add(cLabel7);
        cLabel7.setBounds(310, 15, 50, 17);

        tar_incremE.setMinimumSize(new java.awt.Dimension(10, 18));
        tar_incremE.setPreferredSize(new java.awt.Dimension(10, 18));
        cPanel1.add(tar_incremE);
        tar_incremE.setBounds(360, 15, 40, 17);
        cPanel1.add(Bocul);
        Bocul.setBounds(452, 22, 2, 2);

        cLabel8.setText("Tarifa");
        cLabel8.setPreferredSize(new java.awt.Dimension(33, 18));
        cPanel1.add(cLabel8);
        cLabel8.setBounds(110, 15, 40, 17);

        tar_copiaE.setMaximumSize(new java.awt.Dimension(160, 18));
        tar_copiaE.setMinimumSize(new java.awt.Dimension(160, 18));
        tar_copiaE.setPreferredSize(new java.awt.Dimension(160, 18));
        cPanel1.add(tar_copiaE);
        tar_copiaE.setBounds(150, 15, 150, 17);

        Pcabe.add(cPanel1);
        cPanel1.setBounds(10, 20, 410, 40);

        tar_codiE.setAncTexto(30);
        tar_codiE.setFormato(Types.DECIMAL,"##9");
        tar_codiE.setPreferredSize(new java.awt.Dimension(162, 17));
        Pcabe.add(tar_codiE);
        tar_codiE.setBounds(340, 0, 240, 18);

        cLabel10.setText("Semana");
        Pcabe.add(cLabel10);
        cLabel10.setBounds(220, 0, 50, 18);

        pro_codiE.setAncTexto(80);
        Pcabe.add(pro_codiE);
        pro_codiE.setBounds(190, 60, 380, 17);

        cLabel11.setText("De Fecha");
        Pcabe.add(cLabel11);
        cLabel11.setBounds(2, 2, 55, 18);

        tar_fechaE.setMaximumSize(new java.awt.Dimension(10, 18));
        tar_fechaE.setMinimumSize(new java.awt.Dimension(10, 18));
        tar_fechaE.setPreferredSize(new java.awt.Dimension(10, 18));
        Pcabe.add(tar_fechaE);
        tar_fechaE.setBounds(50, 60, 70, 17);

        BTexto.setText("Texto");
        Pcabe.add(BTexto);
        BTexto.setBounds(430, 30, 90, 20);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        Pprinc.add(Pcabe, gridBagConstraints);

        jt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jt.setMaximumSize(new java.awt.Dimension(100, 200));
        jt.setMinimumSize(new java.awt.Dimension(100, 200));

        org.jdesktop.layout.GroupLayout jtLayout = new org.jdesktop.layout.GroupLayout(jt);
        jt.setLayout(jtLayout);
        jtLayout.setHorizontalGroup(
            jtLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 656, Short.MAX_VALUE)
        );
        jtLayout.setVerticalGroup(
            jtLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 198, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        Pprinc.add(jt, gridBagConstraints);

        ArrayList v1=new ArrayList();
        v1.add("Artic");
        v1.add("Nombre");
        jtArt.setCabecera(v1);
        jtArt.setAnchoColumna(new int[]{50,250});
        jtArt.setAjustarGrid(true);
        jtArt.setBuscarVisible(false);
        jtArt.setMaximumSize(new java.awt.Dimension(80, 60));
        jtArt.setMinimumSize(new java.awt.Dimension(80, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        Pprinc.add(jtArt, gridBagConstraints);

        Ppie.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        Ppie.setMaximumSize(new java.awt.Dimension(392, 58));
        Ppie.setMinimumSize(new java.awt.Dimension(392, 58));
        Ppie.setPreferredSize(new java.awt.Dimension(392, 58));
        Ppie.setLayout(null);

        Baceptar.setText("Aceptar");
        Ppie.add(Baceptar);
        Baceptar.setBounds(10, 2, 90, 25);

        Bcancelar.setText("Cancelar");
        Ppie.add(Bcancelar);
        Bcancelar.setBounds(10, 30, 90, 25);

        Bimpresion.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Bimpresion.setLayout(null);

        cLabel4.setText("Idioma");
        cLabel4.setPreferredSize(new java.awt.Dimension(33, 18));
        Bimpresion.add(cLabel4);
        cLabel4.setBounds(10, 20, 40, 17);

        tar_impriE.setMaximumSize(new java.awt.Dimension(160, 18));
        tar_impriE.setMinimumSize(new java.awt.Dimension(160, 18));
        tar_impriE.setPreferredSize(new java.awt.Dimension(160, 18));
        Bimpresion.add(tar_impriE);
        tar_impriE.setBounds(50, 2, 270, 17);

        Bimpri.setText("Imprimir");
        Bimpri.setToolTipText("Imprimir Tarifa");
        Bimpri.setMaximumSize(new java.awt.Dimension(24, 24));
        Bimpri.setMinimumSize(new java.awt.Dimension(24, 24));
        Bimpri.setPreferredSize(new java.awt.Dimension(24, 24));
        Bimpresion.add(Bimpri);
        Bimpri.setBounds(330, 15, 80, 24);

        opImpRef.setText("Impr. Ref.");
        Bimpresion.add(opImpRef);
        opImpRef.setBounds(240, 20, 80, 17);

        cLabel9.setText("Tarifa");
        cLabel9.setPreferredSize(new java.awt.Dimension(33, 18));
        Bimpresion.add(cLabel9);
        cLabel9.setBounds(10, 2, 40, 17);
        Bimpresion.add(loc_codiE);
        loc_codiE.setBounds(50, 20, 180, 18);

        Ppie.add(Bimpresion);
        Bimpresion.setBounds(130, 2, 420, 50);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        Pprinc.add(Ppie, gridBagConstraints);

        getContentPane().add(Pprinc, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gnu.chu.controles.CButton BTexto;
    private gnu.chu.controles.CButton Baceptar;
    private gnu.chu.controles.CButton Bcancelar;
    private gnu.chu.controles.CPanel Bimpresion;
    private gnu.chu.controles.CButton Bimpri;
    private gnu.chu.controles.CButton Bocul;
    private gnu.chu.controles.CPanel Pcabe;
    private gnu.chu.controles.CPanel Ppie;
    private gnu.chu.controles.CPanel Pprinc;
    private gnu.chu.controles.CLabel cLabel1;
    private gnu.chu.controles.CLabel cLabel10;
    private gnu.chu.controles.CLabel cLabel11;
    private gnu.chu.controles.CLabel cLabel2;
    private gnu.chu.controles.CLabel cLabel3;
    private gnu.chu.controles.CLabel cLabel4;
    private gnu.chu.controles.CLabel cLabel5;
    private gnu.chu.controles.CLabel cLabel6;
    private gnu.chu.controles.CLabel cLabel7;
    private gnu.chu.controles.CLabel cLabel8;
    private gnu.chu.controles.CLabel cLabel9;
    private gnu.chu.controles.CPanel cPanel1;
    private gnu.chu.controles.CGridEditable jt;
    private gnu.chu.controles.Cgrid jtArt;
    private gnu.chu.controles.CComboBox loc_codiE;
    private gnu.chu.controles.CCheckBox opImpRef;
    private gnu.chu.camposdb.proPanel pro_codartE;
    private gnu.chu.camposdb.proPanel pro_codiE;
    private gnu.chu.controles.CTextField pro_nombE;
    private gnu.chu.controles.CLinkBox tar_codiE;
    private gnu.chu.controles.CTextField tar_comenG;
    private gnu.chu.controles.CTextField tar_comrepE;
    private gnu.chu.controles.CComboBox tar_copiaE;
    private gnu.chu.controles.CTextField tar_fecfinE;
    private gnu.chu.controles.CTextField tar_fechaE;
    private gnu.chu.controles.CTextField tar_feciniE;
    private gnu.chu.controles.CTextField tar_fecopE;
    private gnu.chu.controles.CComboBox tar_impriE;
    private gnu.chu.controles.CTextField tar_incremE;
    private gnu.chu.controles.CTextField tar_nusemE;
    private gnu.chu.controles.CTextField tar_preciE;
    // End of variables declaration//GEN-END:variables
   
    @Override
   public boolean next() throws JRException
   {
     try
     {
        if (swInicio)
        {
            swInicio=false;
            return !dtCon1.getNOREG();           
        }
        return dtCon1.next();
     }
     catch (Exception k)
     {
       throw new JRException(k);
     }
   }

  @Override
   public Object getFieldValue(JRField jRField) throws JRException
   {
     try
     {
       String campo = jRField.getName().toLowerCase();
       switch (campo)
       {
           case "pro_nomb":
             if (!swLocaleEmpresa && dtCon1.getString("pro_codart").length()>1)
                 return MantArticulos.getNombreProdLocale(dtCon1.getString("pro_codart"), loc_codiE.getValor(), dtStat);
             else
                return dtCon1.getString(campo);
            case "tar_codi":
               return dtCon1.getInt(campo);               
            case "tar_linea":
               return dtCon1.getInt(campo);               
           case "tar_fecini":
               return dtCon1.getDate(campo);
           case "tar_fecfin":
               return dtCon1.getDate(campo);
           case "pro_codart":  
               return dtCon1.getString(campo);
           case "tar_preci":
               return new BigDecimal(dtCon1.getDouble(campo)+tarIncpre);               
           case "tar_comen":
               return dtCon1.getString(campo);
           case "tar_grupo":
               return dtCon1.getInt(campo);
           case "tar_tipo":
                 return dtCon1.getInt(campo);
           default:
                 throw new Exception("Campo " + campo + " NO encontrado");
       }
     }
     catch (Exception k)
     {
       throw new JRException(k);
     }
   }
}

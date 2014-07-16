package gnu.chu.camposdb;
/**
 *
 * <p>Título: tidCodi2 </p>
 * <p>Descripción: Panel con un textfield para poner los tipos de despiece </p>
 * <p>Copyright: Copyright (c) 2005-2011
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
import gnu.chu.anjelica.despiece.MantTipDesp;
import gnu.chu.anjelica.pad.MantArticulos;
import gnu.chu.utilidades.*;
import gnu.chu.controles.*;
import gnu.chu.sql.*;
import gnu.chu.winayu.AyuTid;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

public class tidCodi2 extends CLinkBox
{
  private boolean ADMIN=false;
  private boolean incluirEstaticos=true;
  AyuTid ayuTid;
  CInternalFrame infFrame;
  JLayeredPane layPane;
  EntornoUsuario EU;
  DatosTabla dt;
  String msgError;
  ArrayList<ArrayList> articList=new ArrayList();
  private int tidActiv=0; // Ver tipos articulos inactivos
  private boolean swIncDespLibre=true;
  private boolean swModoCons=true; // Modo consulta. No limita el despiece libre
  private String deoCodi=null;
  public tidCodi2()
  {
    this(false);
  }

  public tidCodi2(boolean verBoton)
  {
    super(verBoton);
  }


  public void iniciar(DatosTabla dtb, CInternalFrame infFrame,
                      JLayeredPane layPane, EntornoUsuario entUsuario) throws SQLException
  {
    this.setFormato(Types.DECIMAL,"###9");
    this.infFrame=infFrame;
    this.layPane=layPane;
    EU=entUsuario;
    dt=dtb;
    texto.setToolTipText("Pulse F3 para buscar tipos despiece");
   
    swIncDespLibre=dtb.select("select * from tipodesp  as t "+
            " WHERE tid_activ!=0 "+
           " and tid_codi = "+MantTipDesp.LIBRE_DESPIECE);
    
    if (! releer())
      throw new SQLException ("tidCodi2 (iniciar) "+msgError);
    activarEventos();
  }
  private void activarEventos()
  {
      texto.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (e.getKeyCode() == KeyEvent.VK_F3 )
          consTid();
      }
    });
  }
  public boolean controla()
  {
    return controla(true);
  }

  /**
   * Funcion de controlar
   * @param reqFoc realizar req. focus en caso de error.
   *
   * @return boolean -> true si todo es correcto
   */
    public boolean controla(boolean reqFoc) 
    {
        setError(false);
        msgError = "";
        if (isNull())
        {
            msgError = "Debe introducir un Tipo de Despiece";
            setError(true);
            requestFocus();
            return false;
        }
        if (getError())
        {
            msgError = "Tipo de Despiece no Valido";
            setError(true);
            requestFocus();
            return false;
        }
        
        if (getValorInt() == MantTipDesp.LIBRE_DESPIECE && !swModoCons && !ADMIN)
        {
            try
            {
                String s = "select  tid_agrup from tipodesp "
                    + " WHERE tid_codi = " + MantTipDesp.LIBRE_DESPIECE;
                if (!dt.select(s))
                {
                    msgError = "No encontrado tipo despiece libre";
                    setError(true);
                    requestFocus();
                    return false;
                }
                if (dt.getInt("tid_agrup") <= 0)
                    return true; // Sin control
                int numMaxLibres = dt.getInt("tid_agrup");
                s = "select count(*) as cuantos from desporig  WHERE tid_codi = " + MantTipDesp.LIBRE_DESPIECE
                    + (deoCodi == null ? "" : " and deo_codi != " + deoCodi)
                    + " and deo_fecha = current_date and eje_nume = " + EU.ejercicio;
                dt.select(s);
                if (dt.getInt("cuantos") >= numMaxLibres)
                {
                    msgError = "Excedidos número máximo de despieces Libres para hoy.(" + dt.getInt("cuantos") + " >= " + numMaxLibres + ")";
//                    mensajes.mensajeAviso(msgError);
                    setError(true);
                    requestFocus();
                    return false;
                }
            } catch (SQLException ex)
            {
                Logger.getLogger(tidCodi2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
  public void setModoConsulta(boolean modoConsulta )
  {
      swModoCons=modoConsulta;
  }
  public boolean getModoConsulta()
  {
      return swModoCons;
  }
  /**
   * Añade un articulo para los tipos de despiece. 
   * Incluye tambien todos sus equivalentes.
   * 
   * @param proCodi
   * @throws SQLException 
   */
  public void addArticulo(int proCodi) throws SQLException
  {
    
    ArrayList<Integer> al=MantArticulos.getEquivalentes(proCodi, dt);
    
    if (al==null)
    {
        al=new ArrayList();
    }
    al.add(proCodi);
    articList.add(al);
  }
  public void setDeoCodi(String deoCodi)
  {
      this.deoCodi=deoCodi;
  }
  public void setArticulos(ArrayList lista) throws SQLException
  {
    clearArticulos();
    if (lista==null)
        return;
    int nRow=lista.size();
    int art;
    for (int n=0;n<nRow;n++)
    {
        art=Integer.parseInt(lista.get(n).toString().trim());
        if (art!=0)
            addArticulo(art);
    }   
  }
  public ArrayList getArticulos()
  {
      return articList;
  }
  public void clearArticulos()
  {
    articList.clear();
  }
  /**
   * Indica si se deben mostrar los tipos de despieces inactivos
   * Por defecto es false = 0
   * @param tidActiv  -1 ver todos. 0 Tipos despiece Activos, OTROS solo si tid_activ es mayor
   */
  public void setTidActiv(int verInactivo)
  {
      this.tidActiv=verInactivo;
  }
  /**
   * Devuelve si se mostrara los tipos de despieces inactivos
   * @return 
   */
  public int getTidActiv()
  {
      return this.tidActiv;
  }
  /**
   * Rellena el Combo con los Despieces seleccionados.
   * Presenta el codigo del Almacen
   * @return boolean retorna un true si todo ha sido correcto
   */
  public boolean releer() throws SQLException
  {
    setError(false);
    String s="select tid_codi, tid_nomb from tipodesp  as t "+
            (tidActiv==-1?"":" WHERE tid_activ>"+tidActiv)+
             " and tid_codi < 9990 "+
             " ORDER BY t.tid_nomb";
    if (! articList.isEmpty())
    {
        removeAllItems();
//      // Busco Numero de articulos distintos.
//      int artDif=0;
//      for (int n=0;n<articList.size();n++)
//      {
//          if (articList.indexOf(articList.get(n))>=n)
//              artDif++;
//      }
      s="select t.tid_codi, t.tid_nomb from tipodesp as t,tipdesent as te  WHERE "+
        " t.tid_codi=te.tid_codi "+
        (tidActiv==-1?" ":" and tid_activ>"+tidActiv)+
        "  and pro_codi in  (";
     
//      s += " and  (select count(*) from tipdesent where tipdesent.tid_codi = tipodesp.tid_codi "+
//          " and pro_codi in  (" ;
      for (int n=0;n<articList.size();n++)
      {
          ArrayList al=articList.get(n);
          for (int n1=0;n1<al.size();n1++)
             s+=al.get(n1)+",";
      }
//      s=s.substring(0,s.length()-1)+")) >= "+artDif;
      s=s.substring(0,s.length()-1)+") group by t.tid_codi,tid_nomb ";
      if (dt.select(s))
      {
        
        Statement st=dt.getConexion().createStatement();
        ResultSet rs;
        do
        {
            boolean swExist=true;
            for (int n=0;n<articList.size();n++)
            {
              ArrayList al=articList.get(n);
              s="";
              for (int n1=0;n1<al.size();n1++)
                 s+=al.get(n1)+",";
              s=s.substring(0,s.length()-1);
              rs=st.executeQuery("SELECT * FROM tipdesent where tid_codi = "+dt.getInt("tid_codi")+
                   " and pro_codi in ("+s+")");
              if (! rs.next())
              { // No existe ese producto o equivalentes en el tipo despiece. No se insertara.
                  swExist=false;
                  break;
              }
            }
            if (swExist)
              addDatos(dt.getString("tid_codi"),dt.getString("tid_nomb"));
        } while (dt.next());
        st.close();
      }
    }
    else
    {
        dt.select(s);
        addDatos(dt);
    }
    if (isIncluirEstaticos())
    {
        if (swIncDespLibre)
            addDatos(""+MantTipDesp.LIBRE_DESPIECE,"LIBRE");
        addDatos(""+MantTipDesp.AUTO_DESPIECE,"REENVASADO");
        addDatos(""+MantTipDesp.CONGELADO_DESPIECE,"CONGELADO");
    }
    return true;
  }
  public boolean isIncluirEstaticos()
  {
      return incluirEstaticos;
  }
  public void setIncluirEstaticos(boolean incluirEstaticos)
  {
      this.incluirEstaticos=incluirEstaticos;
  }
  public String getMsgError()
  {
    return msgError;
  }
  
  public void consTid()
  {
    try
    {
      if (ayuTid == null)
      {
        ayuTid = new AyuTid(EU, layPane, dt)
        {
          @Override
          public void matar()
          {
            ej_consTid(ayuTid);
          }
        };
        layPane.add(ayuTid);
        ayuTid.setLocation(25, 25);
        ayuTid.iniciarVentana();
      }

      ayuTid.setVisible(true);
      if (infFrame !=null)
      {
        infFrame.setEnabled(false);
        infFrame.setFoco(ayuTid);
      }
      ayuTid.reset();
     
    }
    catch (Exception j)
    {
      if (infFrame != null)
        infFrame.setEnabled(true);
    }
  }

  void ej_consTid(AyuTid aytid)
  {
    if (aytid.getChose())
      texto.setValorInt(aytid.getTidCodi());
  
    texto.requestFocus();
    aytid.setVisible(false);

    if (infFrame != null)
    {
      infFrame.setEnabled(true);
      infFrame.toFront();
      try
      {
        infFrame.setSelected(true);
      }
      catch (Exception k)
      {}
      infFrame.setFoco(null);
      this.requestFocus();
    }
  }
   public boolean isAdmin() {
        return ADMIN;
    }

    public void setAdmin(boolean ADMIN) {
        this.ADMIN = ADMIN;
    }
}

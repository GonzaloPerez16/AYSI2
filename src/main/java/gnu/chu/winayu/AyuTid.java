package gnu.chu.winayu;
/**
 *
 * <p>Título: AyuTid</p>
 * <p>Descripción: Ventana Para ayuda de tipos de despiece </p>
 *
 * <p>Copyright: Copyright (c) 2005-2017
 *  Este programa es software libre. Puede redistribuirlo y/o modificarlo bajo
 *  los términos de la Licencia Pública General de GNU segun es publicada por
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
 * @version 1.0
 */
import gnu.chu.controles.StatusBar;
import gnu.chu.sql.DatosTabla;
import gnu.chu.utilidades.EntornoUsuario;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.ventana;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JLayeredPane;

public class AyuTid extends ventana {
    private boolean chose=false;
    private int tidCodi;
    
    /** Creates new form AyuTid */
    public AyuTid() {
        initComponents();
    }
    
 public AyuTid(EntornoUsuario e, JLayeredPane fr, DatosTabla dt)
  {
    setTitulo("Ayuda Tipos Despiece");
    
    EU = e;
    eje = false;
    vl=fr;
    if (dt != null)
      dtCon1 = dt;
    try
    {
      jbInit();
    }
    catch (Exception k)
    {
      fatalError("constructor: ", k);
      setErrorInit(true);
      return;
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
    this.setSize(new Dimension(606, 526));
  }
    @Override
  public void iniciarVentana() throws Exception
  {
    pro_codiniE.iniciar(dtCon1, this, vl, EU);
    pro_codfinE.iniciar(dtCon1, this, vl, EU);
    activarEventos();
    
    reset();
  }
  public void reset()
  {
      chose=false;
      //tid_nombE.resetTexto();
      tid_nombE.requestFocusLater();
  }
   public boolean getChose() {
        return chose;
    }

    public void setChose(boolean chose) {
        this.chose = chose;
    }
 void activarEventos()
  {

    jt.tableView.addMouseListener(new MouseAdapter()
    {
       @Override
      public void mouseClicked(MouseEvent m)
      {
        if (m.getClickCount() > 1 && jt.isVacio() == false)
        {
            chose=true;
            tidCodi=jt.getValorInt(0);
            matar();
        }
      }

    });

    jt.tableView.addKeyListener(new KeyAdapter()
    {
            @Override
      public void keyPressed(KeyEvent e)
      {
        if ((e.getKeyCode() == KeyEvent.VK_INSERT || e.getKeyCode() == KeyEvent.VK_ENTER) && jt.isVacio() == false)
        {
            chose=true;
            tidCodi=jt.getValorInt(0);
            matar();
        }
      }
    });
    Baceptar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Baceptar_actionPerformed();
            }
        });
  }
  public int getTidCodi()
  {
      return tidCodi;
  }
  void Baceptar_actionPerformed()
  {
     chose=false;
     String s="SELECT tid_codi,tid_nomb FROM tipodesp as td where 1=1 "+
             (tid_nombE.isNull(true)?"": " and upper(tid_nomb) LIKE '%"+tid_nombE.getText()+"%'")+
             " and tid_activ != 0 "+
             (pro_codiniE.isNull()?"":" and tid_codi in (select tid_codi from tipdesent WHERE pro_codi ="+pro_codiniE.getValorInt()+")")+
             (pro_codfinE.isNull()?"":" and tid_codi in (select tid_codi from tipdessal WHERE pro_codi ="+pro_codfinE.getValorInt()+")")+
             " order by tid_nomb ";
     try {
        if (!dtCon1.select(s))
        {
            msgBox("No encontrados despieces para estas condiciones");
            tid_nombE.requestFocus();
            return;
        }
        jt.setDatos(dtCon1);
     } catch (SQLException k)
     {
         Error("Error al buscar despieces",k);
     }           
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

        Pprinc = new gnu.chu.controles.CPanel();
        Pcond = new gnu.chu.controles.CPanel();
        cLabel1 = new gnu.chu.controles.CLabel();
        tid_nombE = new gnu.chu.controles.CTextField();
        cLabel2 = new gnu.chu.controles.CLabel();
        pro_codiniE = new gnu.chu.camposdb.proPanel();
        cLabel3 = new gnu.chu.controles.CLabel();
        pro_codfinE = new gnu.chu.camposdb.proPanel();
        Baceptar = new gnu.chu.controles.CButton("Aceptar",Iconos.getImageIcon("check"));
        jt = new gnu.chu.controles.Cgrid(2);
        { ArrayList v=new ArrayList();
            v.add("Codigo");
            v.add("Nombre");
            jt.setCabecera(v);
            jt.setAnchoColumna(new int[]{40,200});
            jt.setAlinearColumna(new int[]{2,0});
            jt.setAjustarGrid(true);
        }

        Pprinc.setLayout(new java.awt.GridBagLayout());

        Pcond.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        Pcond.setDefButton(Baceptar);
        Pcond.setMaximumSize(new java.awt.Dimension(399, 95));
        Pcond.setMinimumSize(new java.awt.Dimension(399, 95));
        Pcond.setPreferredSize(new java.awt.Dimension(399, 95));
        Pcond.setLayout(null);

        cLabel1.setText("Nombre");
        Pcond.add(cLabel1);
        cLabel1.setBounds(2, 2, 55, 15);

        tid_nombE.setMayusc(true);
        Pcond.add(tid_nombE);
        tid_nombE.setBounds(61, 2, 327, 17);

        cLabel2.setText("Producto Origen ");
        Pcond.add(cLabel2);
        cLabel2.setBounds(0, 20, 100, 15);
        Pcond.add(pro_codiniE);
        pro_codiniE.setBounds(100, 20, 290, 17);

        cLabel3.setText("Producto Final");
        Pcond.add(cLabel3);
        cLabel3.setBounds(0, 40, 100, 15);
        Pcond.add(pro_codfinE);
        pro_codfinE.setBounds(100, 40, 290, 17);
        Pcond.add(Baceptar);
        Baceptar.setBounds(130, 60, 100, 30);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 6);
        Pprinc.add(Pcond, gridBagConstraints);

        jt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jt.setMaximumSize(new java.awt.Dimension(393, 300));
        jt.setMinimumSize(new java.awt.Dimension(393, 300));

        javax.swing.GroupLayout jtLayout = new javax.swing.GroupLayout(jt);
        jt.setLayout(jtLayout);
        jtLayout.setHorizontalGroup(
            jtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        jtLayout.setVerticalGroup(
            jtLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        Pprinc.add(jt, gridBagConstraints);

        getContentPane().add(Pprinc, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gnu.chu.controles.CButton Baceptar;
    private gnu.chu.controles.CPanel Pcond;
    private gnu.chu.controles.CPanel Pprinc;
    private gnu.chu.controles.CLabel cLabel1;
    private gnu.chu.controles.CLabel cLabel2;
    private gnu.chu.controles.CLabel cLabel3;
    private gnu.chu.controles.Cgrid jt;
    private gnu.chu.camposdb.proPanel pro_codfinE;
    private gnu.chu.camposdb.proPanel pro_codiniE;
    private gnu.chu.controles.CTextField tid_nombE;
    // End of variables declaration//GEN-END:variables
}
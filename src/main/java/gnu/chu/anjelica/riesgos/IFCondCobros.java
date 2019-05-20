/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * IFCondCobros.java
 * <p>Descripción: Internal Frame para buscar  cobros segun unas condiciones</p>
 *
 * <p>Created on 03-abr-2009, 18:14:38</p>
* <p>Copyright: Copyright (c) 2005-2009
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
 */
package gnu.chu.anjelica.riesgos;


import gnu.chu.sql.DatosTabla;
import gnu.chu.utilidades.EntornoUsuario;
import gnu.chu.utilidades.Iconos;
import gnu.chu.utilidades.ventana;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Vector;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author cpuente
 */
public class IFCondCobros extends ventana {
    ventana padre=null;
    /** Creates new form IFCondCobros */
    public IFCondCobros() {
        setVisibleCabeceraVentana(false);
//        BasicInternalFrameUI uit = (BasicInternalFrameUI)this.getUI();
//        uit.setNorthPane(null); // Para quitar el titulo a la ventana
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        cPanel1 = new gnu.chu.controles.CPanel();
        Pprinc = new gnu.chu.controles.CPanel();
        PFiltro = new gnu.chu.controles.CPanel();
        cLabel1 = new gnu.chu.controles.CLabel();
        feciniE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        cLabel2 = new gnu.chu.controles.CLabel();
        fecfinE = new gnu.chu.controles.CTextField(Types.DATE,"dd-MM-yyyy");
        serieIniE = new gnu.chu.controles.CTextField(Types.CHAR,"X",1);
        serieFinE = new gnu.chu.controles.CTextField(Types.CHAR,"X",1);
        cLabel5 = new gnu.chu.controles.CLabel();
        empIniE = new gnu.chu.camposdb.empPanel();
        empNomIniE = new gnu.chu.controles.CLabel();
        cLabel7 = new gnu.chu.controles.CLabel();
        cliIniE = new gnu.chu.camposdb.cliPanel();
        fpaCodiE = new gnu.chu.camposdb.fpaPanel();
        cLabel6 = new gnu.chu.controles.CLabel();
        cLabel8 = new gnu.chu.controles.CLabel();
        cli_giroE = new gnu.chu.controles.CComboBox();
        cLabel9 = new gnu.chu.controles.CLabel();
        fvcIniE = new gnu.chu.controles.CTextField(Types.DECIMAL,"#####9");
        cLabel10 = new gnu.chu.controles.CLabel();
        fvcFinE = new gnu.chu.controles.CTextField(Types.DECIMAL,"#####9");
        Bconsulta = new gnu.chu.controles.CButton(Iconos.getImageIcon("pon"));
        jt = new gnu.chu.controles.Cgrid(14);
        Baceptar = new gnu.chu.controles.CButton();
        Bcancelar = new gnu.chu.controles.CButton();
        cPanel2 = new gnu.chu.controles.CPanel();
        BInvSelec = new gnu.chu.controles.CButton();
        cLabel3 = new gnu.chu.controles.CLabel();
        numDocsE = new gnu.chu.controles.CTextField(Types.DECIMAL,"9999");
        cLabel4 = new gnu.chu.controles.CLabel();
        impDocsE = new gnu.chu.controles.CTextField(Types.DECIMAL,"--,---,--9.99");

        org.jdesktop.layout.GroupLayout cPanel1Layout = new org.jdesktop.layout.GroupLayout(cPanel1);
        cPanel1.setLayout(cPanel1Layout);
        cPanel1Layout.setHorizontalGroup(
            cPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        cPanel1Layout.setVerticalGroup(
            cPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        Pprinc.setText("Aceptar");
        Pprinc.setLayout(null);

        PFiltro.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        cLabel1.setText("De Fecha");

        cLabel2.setText("A Fecha");

        cLabel5.setText("Empresa");

        empNomIniE.setBackground(java.awt.Color.orange);
        empNomIniE.setOpaque(true);
        empNomIniE.setPreferredSize(new java.awt.Dimension(41, 17));

        cLabel7.setText("Cliente");

        cLabel6.setText("Forma Pago");

        cLabel8.setText("Giros");

        cLabel9.setText("De Documento");

        cLabel10.setText("A Documento");

        Bconsulta.setText("Añadir");

        org.jdesktop.layout.GroupLayout PFiltroLayout = new org.jdesktop.layout.GroupLayout(PFiltro);
        PFiltro.setLayout(PFiltroLayout);
        PFiltroLayout.setHorizontalGroup(
            PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PFiltroLayout.createSequentialGroup()
                .addContainerGap()
                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(PFiltroLayout.createSequentialGroup()
                        .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, PFiltroLayout.createSequentialGroup()
                                .add(cLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(9, 9, 9)
                                .add(empIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(2, 2, 2)
                                .add(empNomIniE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, PFiltroLayout.createSequentialGroup()
                                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(cLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .add(cLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(cliIniE, 0, 0, Short.MAX_VALUE)
                                    .add(fpaCodiE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 344, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                        .add(4, 4, 4))
                    .add(PFiltroLayout.createSequentialGroup()
                        .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, cLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(cLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, PFiltroLayout.createSequentialGroup()
                                .add(feciniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(cLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(fecfinE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(Bconsulta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 112, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(PFiltroLayout.createSequentialGroup()
                                .add(cli_giroE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 47, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(2, 2, 2)
                                .add(serieIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(2, 2, 2)
                                .add(fvcIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(4, 4, 4)
                                .add(cLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(2, 2, 2)
                                .add(serieFinE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(2, 2, 2)
                                .add(fvcFinE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .add(44, 44, 44))
        );
        PFiltroLayout.setVerticalGroup(
            PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PFiltroLayout.createSequentialGroup()
                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(empIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(empNomIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cliIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(fpaCodiE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(cli_giroE, 0, 0, Short.MAX_VALUE)
                        .add(cLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(serieIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(fvcIniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(cLabel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(serieFinE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(fvcFinE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(cLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(PFiltroLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(feciniE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(fecfinE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(Bconsulta, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        Pprinc.add(PFiltro);
        PFiltro.setBounds(70, 0, 450, 110);

        jt.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        org.jdesktop.layout.GroupLayout jtLayout = new org.jdesktop.layout.GroupLayout(jt);
        jt.setLayout(jtLayout);
        jtLayout.setHorizontalGroup(
            jtLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 698, Short.MAX_VALUE)
        );
        jtLayout.setVerticalGroup(
            jtLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 238, Short.MAX_VALUE)
        );

        Pprinc.add(jt);
        jt.setBounds(0, 110, 700, 240);

        Baceptar.setText("Cancelar");
        Pprinc.add(Baceptar);
        Baceptar.setBounds(200, 360, 120, 19);

        Bcancelar.setText("Aceptar");
        Pprinc.add(Bcancelar);
        Bcancelar.setBounds(40, 360, 120, 19);

        cPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        BInvSelec.setText("Inv. Selección");

        cLabel3.setText("N. Documentos");

        numDocsE.setEnabled(false);

        cLabel4.setText("Imp. Docum.");

        impDocsE.setEnabled(false);

        org.jdesktop.layout.GroupLayout cPanel2Layout = new org.jdesktop.layout.GroupLayout(cPanel2);
        cPanel2.setLayout(cPanel2Layout);
        cPanel2Layout.setHorizontalGroup(
            cPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cPanel2Layout.createSequentialGroup()
                .add(cLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(numDocsE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 49, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(cLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(impDocsE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, cPanel2Layout.createSequentialGroup()
                .addContainerGap(113, Short.MAX_VALUE)
                .add(BInvSelec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(78, 78, 78))
        );
        cPanel2Layout.setVerticalGroup(
            cPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cPanel2Layout.createSequentialGroup()
                .add(cPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(numDocsE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(impDocsE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(BInvSelec, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        Pprinc.add(cPanel2);
        cPanel2.setBounds(380, 350, 320, 60);

        getContentPane().add(Pprinc, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents
public void iniciar(DatosTabla dt,ventana papa,EntornoUsuario EU ) throws Exception
{
    this.EU= EU;
    this.dtCon1=dt;
    this.padre=papa;
    empIniE.iniciar(dt, this, papa.vl, EU);
    empIniE.setLabelEmp(empNomIniE);
    empIniE.setAceptaNulo(false);
    empIniE.setValorInt(EU.em_cod);
    cliIniE.iniciar(dt, this, papa.vl, EU);
    fpaCodiE.iniciar(dt, this, papa.vl, EU);
    cli_giroE.addItem("**","X");
    cli_giroE.addItem("No","N");
    cli_giroE.addItem("Si","S");
    iniciarGrid();
    iniciarEventos();

}
void iniciarEventos()
{
    Bconsulta.addActionListener(new java.awt.event.ActionListener()
   {
     public void actionPerformed(ActionEvent e)
     {
       Bconsulta_actionPerformed();
     }
   });
    BInvSelec.addActionListener(new java.awt.event.ActionListener()
   {
     public void actionPerformed(ActionEvent e)
     {
       Bconsulta_actionPerformed();
     }
   });
    Baceptar.addActionListener(new java.awt.event.ActionListener()
   {
     public void actionPerformed(ActionEvent e)
     {
       Bconsulta_actionPerformed();
     }
   });
    Bcancelar.addActionListener(new java.awt.event.ActionListener()
   {
     public void actionPerformed(ActionEvent e)
     {
       Bconsulta_actionPerformed();
     }
   });
}

private void  Bconsulta_actionPerformed ()
{
   String cc,s = "";
   try
   {
   if (fvcIniE.getValorInt() > fvcFinE.getValorInt())
   {
     mensajeErr("Documento Final NO puede ser inferior a Inicial");
     fvcFinE.setValorInt(fvcIniE.getValorInt());
     fvcFinE.requestFocus();
     return;
   }

   if (!empIniE.controla())
   {
       mensajeErr(empNomIniE.getText());
       return;
   }
   
   cc = " and f.emp_codi = " + empIniE.getValorInt();

   if (!cliIniE.isNull())
     cc += " and cl.cli_codi = " + cliIniE.getValorInt();

   if (!fpaCodiE.isNull())
     cc += " and cl.fpa_codi = " + fpaCodiE.getValorInt();
   if (!cli_giroE.getValor().equals("X"))
     cc += " and cl.cli_giro = '" + cli_giroE.getValor() + "'";

   s = "SELECT 'A' as albfra, f.emp_codi, avc_ano as ano,avc_serie as serie,avc_nume as nume," +
       " avc_fecalb as fecha,avc_impalb as impdoc,avc_impcob as impcob, " +
       "avc_impalb-avc_impcob as imppen, " +
       "avc_cobrad AS tocob,cl.cli_codi,cl.cli_nomb," +
       " cl.cli_dipa1,cl.cli_dipa2,fp.fpa_dia1,fp.fpa_dia2,fp.fpa_dia3" +
       " FROM v_albavec f,clientes as cl,v_forpago as fp WHERE  fvc_nume = 0" +
       " and f.cli_codi = cl.cli_codi " +
       (EU.isRootAV()?"":" AND f.div_codi > 0 ")+
       " and fp.fpa_codi = cl.fpa_codi "+
       " AND avc_cobrad = 0 "+
       " AND avc_cobrad = 0 " +
       " and avc_impalb <> 0";
   if (!feciniE.isNull())
     s += " and f.avc_fecalb >= TO_DATE('" + feciniE.getText() + "','dd-MM-yyyy')";
   if (!fecfinE.isNull())
     s += " and f.avc_fecalb <= TO_DATE('" + fecfinE.getText() +
         "','dd-MM-yyyy')";
   if (!serieIniE.isNull(true))
     s += " and f.avc_serie >= '" + serieIniE.getText() + "'";
   if (!serieFinE.isNull(true))
     s += " and f.avc_serie <= '" + serieFinE.getText() + "'";
   s+=cc;

   s += " UNION ALL" +
       " SELECT 'F' as albfra, f.emp_codi, fvc_ano as ano,fvc_serie as serie,fvc_nume as nume," +
       " fvc_fecfra as fecha,fvc_sumtot as impdoc,fvc_impcob as impcob, " +
       "fvc_sumtot-fvc_impcob as imppen, " +
       "fvc_cobrad AS tocob,cl.cli_codi,cl.cli_nomb,"+
       " cl.cli_dipa1,cl.cli_dipa2,fp.fpa_dia1,fp.fpa_dia2,fp.fpa_dia3" +
       " FROM v_facvec as f,clientes cl,v_forpago as fp WHERE " +
       " fvc_sumtot <> 0" +
       " and fp.fpa_codi = cl.fpa_codi "+
       " AND fvc_cobrad = 0 "+
       " and f.cli_codi = cl.cli_codi ";
   if (!feciniE.isNull())
     s += " and f.fvc_fecfra >= TO_DATE('" + feciniE.getText() +
         "','dd-MM-yyyy')";
   if (!fecfinE.isNull())
     s += " and f.fvc_fecfra <= TO_DATE('" + fecfinE.getText() +
         "','dd-MM-yyyy')";
   if (fvcIniE.getValorInt()>0)
     s += " and f.fvc_nume >= " + fvcIniE.getValorInt();
   if (fvcFinE.getValorInt()>0)
     s += " and f.fvc_nume <= " + fvcFinE.getValorInt();
   s+=cc;
   debug(s);

     if (!dtCon1.select(s))
     {
       mensajeErr("No encontrados Documentos para estos criterios");
       return;
     }

   
     do
     {

       Vector v=new Vector();
       if (!exisLinea(dtCon1))
       {
         v.addElement(dtCon1.getString("albfra"));
         v.addElement(dtCon1.getString("emp_codi"));
         v.addElement(dtCon1.getString("ano"));
         v.addElement(dtCon1.getString("serie"));
         v.addElement(dtCon1.getString("nume"));
         v.addElement(dtCon1.getFecha("fecha","dd-MM-yyyy"));
         v.addElement(dtCon1.getString("impdoc"));
         v.addElement(dtCon1.getString("impcob"));
         v.addElement(dtCon1.getString("imppen"));
         v.addElement("N"); // Pend. a NO
         v.addElement(dtCon1.getString("cli_codi"));
         v.addElement(dtCon1.getString("cli_nomb"));
         clFactCob.calDiasVto(dtCon1.getInt("fpa_dia1"),
                              dtCon1.getInt("fpa_dia2"),
                              dtCon1.getInt("fpa_dia3"),
                              dtCon1.getInt("cli_dipa1"),
                              dtCon1.getInt("cli_dipa2"), 0,
                              dtCon1.getFecha("fecha", "dd-MM-yyyy"));
         v.addElement(clFactCob.diasVto[0]);
         v.addElement("S");
         jt.addLinea(v);
       }
     } while (dtCon1.next());
     
//     verDatCob(jt.getSelectedRow());
     actSumGrid();
   } catch (Throwable k)
   {
     Error("Error al cargar datos Masivos",k);
   }
 }

 boolean exisLinea(DatosTabla dt) throws SQLException
 {
   int nRow=jt.getRowCount();
   for (int n=0;n<nRow;n++)
   {
     if (dt.getString("albfra").equals(jt.getValString(n,0)) &&
         dt.getInt("emp_codi")==jt.getValorInt(n,1) &&
         dt.getInt("ano")==jt.getValorInt(n,2) &&
         dt.getString("serie").equals(jt.getValString(n,3)) &&
         dt.getInt("nume")==jt.getValorInt(n,4))
       return true;
   }
   return false;
 }
 void actSumGrid()
 {
   double iFra=0;
   int nFra=0;
   int nRow=jt.getRowCount();
   for (int n=0;n<nRow;n++)
   {
     if (jt.getValBoolean(n, 13))
           continue;    
       nFra++;
       iFra +=  jt.getValorDec(n,8);
   }     
   impDocsE.setValorDec(iFra);
   numDocsE.setValorDec(nFra);
 }

private void iniciarGrid()
{
    Vector v=new Vector();
   
    v.add("F/A"); // 0
    v.add("Emp"); // 1
    v.add("Ejer"); // 2
    v.add("Ser."); // 3
    v.add("Numero"); // 4
    v.add("Fec.Doc."); // 5
    v.add("Imp.Doc"); // 6
    v.add("Imp.Cobr."); // 7
    v.add("Imp.Pend."); // 8
    v.add("Pend.");  // 9 Pendiente
    v.add("Cliente"); // 10
    v.add("Nombre Cliente"); // 11
    v.add("Fec.Vto"); // 12
    v.add("INC");// 13
    jt.setCabecera(v);
    jt.setAnchoColumna(new int[]{25,30,40,30,60,90,90,90,90,30,60,100,90,40});
    jt.setAlinearColumna(new int[]{1,2,2,1,2,1,2,2,2,1,2,0,1,0});
    jt.setFormatoColumna(6,"----,--9.99");
    jt.setFormatoColumna(7,"----,--9.99");
    jt.setFormatoColumna(8,"----,--9.99");
    jt.setFormatoColumna(9,"B0-");
    jt.setFormatoColumna(13,"BSN");
    jt.setAjustarGrid(true);
    //jt.ajustar(false);
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private gnu.chu.controles.CButton BInvSelec;
    private gnu.chu.controles.CButton Baceptar;
    private gnu.chu.controles.CButton Bcancelar;
    private gnu.chu.controles.CButton Bconsulta;
    private gnu.chu.controles.CPanel PFiltro;
    private gnu.chu.controles.CPanel Pprinc;
    private gnu.chu.controles.CLabel cLabel1;
    private gnu.chu.controles.CLabel cLabel10;
    private gnu.chu.controles.CLabel cLabel2;
    private gnu.chu.controles.CLabel cLabel3;
    private gnu.chu.controles.CLabel cLabel4;
    private gnu.chu.controles.CLabel cLabel5;
    private gnu.chu.controles.CLabel cLabel6;
    private gnu.chu.controles.CLabel cLabel7;
    private gnu.chu.controles.CLabel cLabel8;
    private gnu.chu.controles.CLabel cLabel9;
    private gnu.chu.controles.CPanel cPanel1;
    private gnu.chu.controles.CPanel cPanel2;
    private gnu.chu.camposdb.cliPanel cliIniE;
    private gnu.chu.controles.CComboBox cli_giroE;
    private gnu.chu.camposdb.empPanel empIniE;
    private gnu.chu.controles.CLabel empNomIniE;
    private gnu.chu.controles.CTextField fecfinE;
    private gnu.chu.controles.CTextField feciniE;
    private gnu.chu.camposdb.fpaPanel fpaCodiE;
    private gnu.chu.controles.CTextField fvcFinE;
    private gnu.chu.controles.CTextField fvcIniE;
    private gnu.chu.controles.CTextField impDocsE;
    private gnu.chu.controles.Cgrid jt;
    private gnu.chu.controles.CTextField numDocsE;
    private gnu.chu.controles.CTextField serieFinE;
    private gnu.chu.controles.CTextField serieIniE;
    // End of variables declaration//GEN-END:variables

}
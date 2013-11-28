package gnu.chu.controles;

/*
<p>Título: JTable Editable Avanzado</p>
 * <p>Descripción: Extiende de CGrid. permite modificar el valor de los campos
 * facilmente. Controlando validacion en cambio de columnas y filas.
 * </p> 
 * <p>Copyright: Copyright (c) 2005-2013
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
 * @author chuchi P.
 * @version 3.1  (9/1/2013)
 */
import gnu.chu.eventos.GridEvent;
import gnu.chu.eventos.GridListener;
import gnu.chu.interfaces.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.*;


public class CGridEditable extends Cgrid implements CQuery {
    private boolean query=false;
    final private  String TIPO_LINKBOX="L";
    final private  String TIPO_TEXTFIELD="T"; 
    final private  String TIPO_COMBOBOX="C";
    
    ArrayList grListener=new ArrayList();
    private boolean lineaEditable = true;
    private boolean ponValoresEnabled = false;
    private boolean ponValoresInFocus = false;
    boolean reqFocusEdit = false;
    public boolean binsert = false;
    boolean procInsLinea = true;
    int colNueva = 0; // Col. donde ir cuando se inserta una Linea
    private int eatCambioLinea = 0;
    int eatCambioCol = 0;
    int colIni = 0;
    int colFin = 0;
    int nColErr = 0; // Columna a la que ir en caso de error.
    boolean swEvent = false; // Procesando Eventos.
    private // Procesando Eventos.
            int antRow = 0;
    private int antColumn = 0;
//  int nColuT = 0;
    ArrayList campos = null;
    ArrayList tCampo;
    ArrayList vCampo = null; // Valores por defecto
    boolean swIniciar = false;
//  boolean canInsertLinea= true; // Indica si se pueden insertar Nuevas Lineas
//  boolean canBorrarLinea=true; // Indica si se pueden borrar lineas.

  public CGridEditable()
  {
    super();
  }
  
  public CGridEditable(int numcol)
  {    
    iniciar(numcol);
  }

  public void iniciar(int numcol)
  {
    try {
      if (swIniciar)
          throw new Exception("No se puede llamar a esta funcion más que una vez");
      setNumColumnas(numcol);
    } catch (Exception k)
    {
       System.out.println(k.getMessage());
       return;
    }
    swIniciar=true;
    setBotonBorrar();
    setBotonInsert();

    tableView.setRowSelectionAllowed(false);
    tableView.setColumnSelectionAllowed(false);
    tableView.setCellSelectionEnabled(true);
    tableView.getTableHeader().setResizingAllowed(false);
    tableView.getTableHeader().setReorderingAllowed(false);
    super.setOrdenar(false);
    activarEventos();
    this.setEnabled(false);
  }

  private void activarEventos()
  {
    this.setButton(KeyEvent.VK_F8, null);
    this.setButton(KeyEvent.VK_F7, null);
    tableView.getColumnModel().addColumnModelListener(new
        TableColumnModelListener()
    {
      public void columnAdded(TableColumnModelEvent e)
      {}

      public void columnRemoved(TableColumnModelEvent e)
      {}

      public void columnMoved(TableColumnModelEvent e)
      {}

      public void columnMarginChanged(ChangeEvent e)
      {}

      public void columnSelectionChanged(ListSelectionEvent e)
      {
        if (e.getValueIsAdjusting()) // || antColumn==getSelectedColumn())
          return;
        if (eatCambioCol > 0)
        {
          eatCambioCol--;
          return;
        }
//        System.out.println(" Col: "+getValString(0,5)+ " - "+getValString(1,5));
        procCambiaCol(e);
      }
    });
    tableView.getSelectionModel().addListSelectionListener(new
        ListSelectionListener()
    {
      public void valueChanged(ListSelectionEvent e)
      {
        if ( getEatCambioLinea() > 0)
        {
          setEatCambioLinea(getEatCambioLinea() - 1);
          return;
        }
        if (e.getValueIsAdjusting()) // && e.getFirstIndex() == e.getLastIndex())
          return;
        if (!puedeCambiarLinea())
        {
          if (getSelectedColumn() != getAntColumn())
            eatCambioCol++;
          return;
        }
        cambiaLinea();
      }
    });

    tableView.addKeyListener(new KeyAdapter()
    {
      @Override
      public void keyPressed(KeyEvent e)
      {
        if (!isEnabled())
          return;
        if ( ( (JComponent) campos.get(tableView.getSelectedColumn())).
            isEnabled())
        {
//          System.out.println("Tecla consumida "+e.getKeyChar());
          e.consume();
          return;
        }
        switch (e.getKeyCode())
        {
          case KeyEvent.VK_F8: // Borrar Linea Activa.
            SwingUtilities.invokeLater( new Thread()
            {
                    @Override
              public void run()
              {
                Bborra.doClick();
              }
            });
            break;
          case KeyEvent.VK_F7: // Insertar Nueva Linea
            SwingUtilities.invokeLater( new Thread()
            {
                    @Override
              public void run()
              {
                Binser.doClick();
              }
            });
        }
      }
    });
  }

  void procCambiaCol(ListSelectionEvent e)
  {
    int col = getSelectedColumn();
    if (isEnabled() && campos != null)
    {
      if (col == getAntColumn() && e != null)
      {
        if (e.getFirstIndex() != col)
          col = e.getFirstIndex();
        else if (e.getLastIndex() != col)
          col = e.getLastIndex();
      }
//      System.out.println("antColumn: " + antColumn);
      cambiaColumna0(getAntColumn(),col);
    }
    cambiaLinea();
    setAntColumn(col);
  }

  boolean puedeCambiarLinea()
  {
    if (!isEnabled() || campos == null || swEvent || getAntRow() == getSelectedRow())
    {
      if (!isEnabled())
        afterCambiaLineaDis(getSelectedRowDisab());
      return true;
    }
    swEvent = true;
    if (getSelectedRow() == 0 && TABLAVACIA)
      TABLAVACIA = false;
//    System.out.println("Antigua Row: "+antRow);
    ponValores(getAntRow());//,true,true);

    if ( (nColErr = cambiaLinea1(getAntRow(), getAntColumn())) >= 0)
    {
      swEvent = false;
      requestFocus(getAntRow(),nColErr);
      return false;
    }
//    ponValores(getSelectedRow(), false, false);
    if (getAntColumn() != getSelectedColumn())
    {
      eatCambioCol++;
      cambiaColumna0(getAntColumn(),getSelectedColumn(), getAntRow());
    }
    else
      cambiaColumna0( getAntColumn(),getSelectedColumn(), getAntRow());

    setAntRow(getSelectedRow());
    setAntColumn(getSelectedColumn());
    ponValores(getAntRow(), false, false);
    afterCambiaLinea0();
    swEvent = false;
    return true;
  }

  void cambiaLinea()
  {
    if (isEnabled() && campos != null)
    {
      if (tableView.getSelectedRow()<0 || tableView.getSelectedColumn()<0)
        return;
      if (tableView.isCellEditable(tableView.getSelectedRow(),
                                   tableView.getSelectedColumn()))
      {
        tableView.editCellAt(tableView.getSelectedRow(),
                             tableView.getSelectedColumn());
      }
    }
  }
 @Override
  protected void Bborra_actionPerformed()
  {
    setEatCambioLinea(2);
    int rw;
    if (!TABLAVACIA)
    {
      if (tableView.isEditing())
        tableView.editingStopped(new ChangeEvent(this));
      rw = tableView.getSelectedRow();

      if (!deleteLinea(rw, tableView.getSelectedColumn()))
      {
        requestFocus(getSelectedRow(), getSelectedColumn());
        return;
      }
      int nRow = getRowCount();
      removeLinea(rw);
      boolean swVacio=false;
      if (TABLAVACIA)
        swVacio=true;
      boolean vacio=isVacio();
      if (rw == nRow - 1)
        rw = (rw > 0) ? rw - 1 : 0;
      requestFocus(rw, getSelectedColumn());
      ponValores();
      resetCambio();
      afterDeleteLinea();
      afterCambiaLinea0();
      if (vacio)
        afterInsertaLinea0(true);
            setAntRow(rw);
      TABLAVACIA=swVacio;
    }
    else
    {
      requestFocus(0, getSelectedColumn());
            setAntRow(0);
    }
  }
  /**
   * Añadir listener sobre eventos del grid.
   * @param gridListener
   */
  public void addGridListener(GridListener gridListener)
  {
    grListener.add(gridListener);
  }
   public void removeGridListener(GridListener gridListener)
  {
    grListener.remove(gridListener);
  }
   /**
    *
    * Procesa los cambios en el grid (cambio columna o cambio linea)
    * ademas de si se ha insertado linea o no.
    *
    * @param ev GridEvent con los parametros q indican tipo evento
    * @param swColu Es evento tipo cambio Columna
    */
  protected void processGridEvent(GridEvent ev, boolean swColu)
  {
    for (int i = 0; i < grListener.size(); i++)
    {
        if (swColu)
          ( (GridListener) grListener.get(i)).cambioColumna(ev);
        else
          ( (GridListener) grListener.get(i)).cambiaLinea(ev);
    }
  }
  /**
   * Esta rutina sera llamda, cada vez que se borra la linea.
   * Si retorna false, NO SE BORRARA.
   * @param  row linea
   * @param   col columna
   * @return true si puede borrar linea
   */
    @Override
    public boolean deleteLinea(int row, int col) {
        if ( getQuery())
         return false;
        GridEvent grEvent = new GridEvent(this);
        grEvent.setColumna(col);
        grEvent.setLinea(row);
        for (int i = 0; i < grListener.size(); i++) {
            ((GridListener) grListener.get(i)).deleteLinea(grEvent);
            if (! grEvent.getPuedeBorrarLinea())
                return false;
        }
        return canBorrarLinea;
    }

  public void setCanDeleteLinea(boolean canDelete)
  {
    Bborra.setEnabled(canDelete);
    canBorrarLinea = canDelete;
  }

  public boolean getCanDeleteLinea()
  {
    return canBorrarLinea;
  }

    @Override
  protected void Binser_actionPerformed(boolean insLinea)
  {
    binsert = true;
    int cl = -1;
    int colAnt = getSelectedColumn();
    int rw;
    if (tableView.isEditing())
    {
      if (tCampo.get(colAnt).toString().compareTo("T") == 0)
      {
        if (( (CTextField) campos.get(colAnt)).isEnabled() && ( (CTextField) campos.get(colAnt)).isEditable())
          ( (CTextField) campos.get(colAnt)).procesaSalir();
        if ( ( (CTextField) campos.get(colAnt)).getError() &&
            ( (CTextField) campos.get(colAnt)).isEnabled() &&
            ( (CTextField) campos.get(colAnt)).isEditable())
        {
          return;
        }
      }
      tableView.editingStopped(new ChangeEvent(this));
    }

    boolean il = insertaLinea1(getSelectedRow(), getSelectedColumn());
    if (il == true)
    {
      ponValores(getSelectedRow());
      cl = cambiaLinea1(getSelectedRow(), getSelectedColumn());
    }
    if (cl >= 0 || !il)
    {
      requestFocus(getSelectedRow(), cl);
      binsert = false;
      return;
    }
    eatCambioCol++;
    cambiaColumna0(getSelectedColumn(),colNueva);
        setEatCambioLinea(2);
    ArrayList v = new ArrayList();
    insLinea(v);
    rw = getSelectedRow();
    addLinea(v, rw);
    requestFocus(rw, colNueva);
    afterInsertaLinea0(insLinea);
    afterCambiaLinea0();
    binsert = false;
  }

  public void setLineaEditable(boolean linEditable)
  {
    if (campos==null)
      return;
    lineaEditable=linEditable;
    for (int n=0;n<vCampo.size();n++)
    {
      if (campos.get(n) instanceof CEditable)
          ( (CEditable) campos.get(n)).setEditableParent(lineaEditable);
    }
  }

  public boolean isLineaEditable()
  {
    return this.lineaEditable;
  }

    @Override
  public void setEnabled(boolean enab)
  {
    super.setEnabled(enab);

  
    setEditable(enab);
    Pboton.setEnabled(enab);
//    Binser.setEnabled(enab);
    if (!enab)
      tableView.editingStopped(new javax.swing.event.ChangeEvent(tableView));
  }
  /**
   * Un grieditable nunca puede ser ordenable.
   * @param ordenable
   */
    @Override
  public void setOrdenar(boolean ordenable)
  {
      swOrden=false;
  }
  /**
   * Indica si se pueden ajustar el ancho de las columnas con el raton
   *
   * @param ajAncCol Ajustar s/n
   */
  public void setAjusAncCol(boolean ajAncCol)
  {
    tableView.getTableHeader().setResizingAllowed(ajAncCol);
  }

  public boolean getAjusAncCol()
  {
    return tableView.getTableHeader().getResizingAllowed();
  }
  /**
   * @deprecated
   * @see setCampos(ArrayList)
   * @param v 
   */
  public void setCampos(Vector v) throws Exception
  {
      setCampos(new ArrayList(v));
  }
  /**
   * Establece los campos sobre los que se editara.
   * Si el JComponent devuelve false en  el getRequestFocusEnabled() cuando
   * se camabie el campo anterior a este; Este campo sera ignorado.
   * Es decir si estamos en el campo 1 y el campo 2 esta puesto con setRequestFocusEnabled(false)
   * al pulsar TAB o ENTER se pasara al campo 3, no al 2. Sin embargo este campo se podra editar
   * moviendose con el raton o con el cursor atras (SHIFT+TAB)
   * @param v Vector con JComponent. Actualmente soporta JTextField, JComboBox, JCheckBox y
   * JLinkBox
   * @throws Exception En caso de que el numero de campos mandados o los tipos de estos
   * no sean correctos.
   */
  public void setCampos(ArrayList v) throws Exception
  {
      if (!swIniciar)
          throw new Exception("Llame primero a la funcion Iniciar");
    if (v.size() != nCol)
    {
      msgError = "(setCampos) No de Campos(" + v.size() +
          ") NO coincide con No Columnas(" + nCol + ") del Grid";
      setEnabled(true);
      throw new Exception(msgError);
    }
    campos = v;
    tCampo = new ArrayList();
    vCampo = new ArrayList();
    colIni = nCol;
    colFin = 0;
    DefaultCellEditor dce;
    for (int n = 0; n < nCol; n++)
    {
      final Component comp = (Component) v.get(n);
      if (Class.forName("gnu.chu.controles.CButton").isAssignableFrom(comp.
          getClass()))
      { 
        ((CButton) comp).setMargin(new Insets(0,0,0,0));
        tableView.getColumn(tableView.getColumnName(n)).setCellRenderer(new CButton());
        tableView.getColumn(tableView.getColumnName(n)).setCellEditor(
            new miCellEditor( (CButton) comp));
        vCampo.add( ( (CButton) comp).getText());
        tCampo.add("b");
//        ( (CLinkBox) comp).texto.setBackground(Color.yellow);
//        ( (CLinkBox) comp).texto.setForeground(Color.black);
        ( (CButton) comp).addActionListener(new ActionListener()
        {
                    @Override
          public void actionPerformed(ActionEvent e)
          {
            javax.swing.SwingUtilities.invokeLater(new Thread()
            {
                            @Override
              public void run()
              {
                requestFocusSelected();
              }
            });
          }
        });
      }

      if (Class.forName("gnu.chu.controles.CLinkBox").isAssignableFrom(comp.
          getClass()))
      {
        tableView.getColumnModel().getColumn(n).setCellEditor( (CLinkBox) comp);
        vCampo.add( ( (CLinkBox) comp).getText());
        ( (CLinkBox) comp).setGridEditable(this);
        tCampo.add("L");
        ( (CLinkBox) comp).texto.setBackground(Color.yellow);
        ( (CLinkBox) comp).texto.setForeground(Color.black);
        ( (CLinkBox) comp).texto.addKeyListener(new tfKeyListener( (CEditable)
            comp, n, this));
        ( (CLinkBox) comp).texto.setGridEditable(this);
      }

      if (Class.forName("gnu.chu.controles.CComboBox").isAssignableFrom(comp.
          getClass()))
      {
        tableView.getColumnModel().getColumn(n).setCellEditor(new
            DefaultCellEditor( (CComboBox) v.get(n)));
        ( (CComboBox) comp).setGridEditable(this);
        vCampo.add( ( (CComboBox) comp).getText());

        tCampo.add("C");

      }
      if (Class.forName("gnu.chu.controles.CCheckBox").isAssignableFrom(comp.
          getClass()))
      {
        if( Formato[n].equals(""))
              setFormatoColumna(n, "B"+((CCheckBox) comp).getCharSelect()+
                            ((CCheckBox) comp).getCharNoSelect());
        tableView.getColumnModel().getColumn(n).setCellEditor(new
            DefaultCellEditor( (CCheckBox) comp));
        if (Formato[n].equals("") || Formato[n].charAt(0) != 'B')
          vCampo.add( ( (CCheckBox) comp).getSelecion());
        else
          vCampo.add(Boolean.valueOf(( (CCheckBox) comp).isSelected()));

        comp.setBackground(Color.yellow);
        comp.setForeground(Color.black);

        tCampo.add("B");
        ( (CCheckBox) comp).setHorizontalAlignment(AbstractButton.CENTER);
        ( (CCheckBox) comp).setGridEditable(this);
        comp.addKeyListener(new tfKeyListener( (CEditable) comp, n, this));
        ( (CCheckBox) comp).addActionListener(new ActionListener()
        {
                    @Override
          public void actionPerformed(ActionEvent e)
          {
            javax.swing.SwingUtilities.invokeLater(new Thread()
            {
                            @Override
              public void run()
              {
                requestFocusSelected();
              }
            });
          }
        });
      }

      if (Class.forName("gnu.chu.controles.CTextField").isAssignableFrom(comp.
          getClass()))
      {
        tableView.getColumnModel().getColumn(n).setCellEditor(new
            DefaultCellEditor( (CTextField) comp)
        {
                    @Override
          public Object getCellEditorValue()
          {
            if (getQuery())
                return ( (CTextField) comp).getText();
            else
                return ( (CTextField) comp).getTexto();
          }
                    @Override
          public boolean stopCellEditing()
          {
            if (! ((CTextField) comp).puedoSalir())
               return false;
            return super.stopCellEditing();
          }
        });
        vCampo.add( ( (CEditable) comp).getText());
        ( (CTextField) comp).setGridEditable(this);
        ( (CTextField) comp).setRowGrid(n); 
//        if ( ( (CTextField) comp).getTipoCampo()==Types.DATE)
//          setFormatoColumna(n, ((CTextField) comp).getFormato() );
        tCampo.add("T");
        comp.setBackground(Color.yellow);
        comp.setForeground(Color.black);
        comp.addKeyListener(new tfKeyListener( (CEditable) comp, n, this));
      }
      if (comp.isEnabled())
        comp.addFocusListener(new focusAdaptGrid(this, n));
      setCellEditable(comp.isEnabled(), n);
      if (comp.isEnabled() && colIni > n)
        colIni = n;
      if (comp.isEnabled())
        colFin = n;
    }
    if (colNueva == 0)
      colNueva = colIni;
//    ponValores(0);
  }
  /**
   * Pone a los campos el mismo formato que tengan los Objetos
   * mandados en setCampos (solo si son CTextField).
   */
  public void setFormatoCampos()
  {
      for (int n=0;n<this.getColumnCount();n++)
      {
            try {
                if (Class.forName("gnu.chu.controles.CTextField").isAssignableFrom(campos.get(n).getClass())) 
                {
                    if (((CTextField) campos.get(n)).getTipoCampo() == Types.DECIMAL) {                            
                        setFormatoColumna(n, ((CTextField) campos.get(n)).getFormato());
                    }
                    if (((CTextField) campos.get(n)).getTipoCampo() == Types.DATE) {
                        setFormatoColumna(n, ((CTextField) campos.get(n)).getFormato());
                    }
                }
                 if (Class.forName("gnu.chu.controles.CCheckBox").isAssignableFrom(campos.get(n).getClass())) 
                 {                      
                        setFormatoColumna(n, "B"+((CCheckBox) campos.get(n)).getCharSelect()+
                            ((CCheckBox) campos.get(n)).getCharNoSelect());
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CGridEditable.class.getName()).log(Level.SEVERE, null, ex);
            }
      }

  }
  public void requestFocusSuper(int row, int col)
  {
        setAntColumn(col);
    super.requestFocus(row, col);
  }

  public void requestFocusFinal()
  {
    requestFocus(getRowCount() - 1, 0);
  }

  public void requestFocusInicioLater()
  {
    SwingUtilities.invokeLater(new Thread()
    {
      public void run()
      {
        requestFocusInicio();
      }
    });

  }
    @Override
  public void requestFocusInicio()
  {
    if (!isEnabled())
    {
      super.requestFocus(0, 0);
      return;
    }
    setLineaEditable(true);
    setAntColumn(colIni);
    setAntRow(0);
    super.requestFocus(0, colNueva);
    tableView.editCellAt(0, colNueva);
    tableView.scrollRectToVisible(tableView.getCellRect(0, colNueva, true));
    if (! this.isVacio())
      this.ponValores(0);
    resetCambio();
  }
  public void requestFocus(int col)
  {
      requestFocus(getSelectedRow(),col);
  }
    @Override
  public void requestFocus(int row, int col)
  {
    if (!isEnabled() || campos == null)
    {
      super.requestFocus(row, col);
      return;
    }

    if (row < 0)
      row = 0;

    if (col < 0)
      col = 0;
    if (col >= nCol)
      col = nCol - 1;
    int col1 = -1;
    col1=getProxColEdi(col-1);

    if (col1==col-1)
      col1=getProxColEdi(-1);

    if (col1 == -1)
      col1 = 0;
    super.requestFocus(row, col1);
    setAntColumn(col1);
    if (getAntRow()!=row)
    {
      if (! this.isVacio())
      {
        if (isPonValoresInFocus())
          this.ponValores(row);
      }
      resetCambio(); // Si cambio la linea reseteo los flags de cambio
    }
    setAntRow(row);
    int rowEdit = row;
    int colEdit = col1;
    try {
        tableView.editCellAt(rowEdit, colEdit);
        tableView.scrollRectToVisible(tableView.getCellRect(rowEdit, colEdit, true));
    } catch (ArrayIndexOutOfBoundsException k)
    {
         Logger.getLogger(CGridEditable.class.getName()).log(Level.SEVERE, "Error en editCell. Row: "+rowEdit+" Col:"+colEdit, k);
    }


/*    SwingUtilities.invokeLater(new Thread()
    {
      public void run()
      {
        tableView.editCellAt(rowEdit, colEdit);
        tableView.scrollRectToVisible(tableView.getCellRect(rowEdit, colEdit, true));
      }
    });

*/
  }
     /**
    * Función para eliminar la fila especificada del grid.
    */
    @Override
     public boolean removeLinea(int fila){
       try {
         if (isEnabled())
         {
          if (tableView.isEditing())
            tableView.editingStopped(new javax.swing.event.ChangeEvent(tableView));
         }
         super.removeLinea(fila);
 
       } catch (ArrayIndexOutOfBoundsException k)
       {
         Logger.getLogger(CGridEditable.class.getName()).log(Level.SEVERE, "Error en removeLinea Fila: "+fila, k);
       }
       return true;
     }

  public void requestFocusLater()
  {
    requestFocusLater(getSelectedRow(),getSelectedColumn());
  }

    @Override
  public void requestFocusLater(final int row,final int col)
  {
    SwingUtilities.invokeLater(new Thread()
       {
            @Override
         public void run()
         {
           CGridEditable.this.requestFocus();
           CGridEditable.this.requestFocus(row,col);
         }
       });
  }

  /**
   * Pasa al Grid los valores de los campos (CTextField, CComboBox, etc.)
   *
   * Se llamara desde fuera, cuando se quiera dar por finalizada la carga
   * en el grid.
   * @deprecated usar salirGrid
   */
  public void procesaAllFoco()
  {
    salirGrid();
  }

  /**
   * Pasa al Grid los valores de los campos (CTextField, CComboBox, etc.)
   * de la linea mandada al grid.
   *
   * Se llamara desde fuera, cuando se quiera dar por finalizada la carga
   * en el grid.
   * @param linea procesa foco en esta linea
   */
  public void procesaAllFoco(int linea)
  {
    ArrayList v = new ArrayList();
    for (int nCampo = 0; nCampo < nCol; nCampo++)
      v.add(actValGrid(linea, nCampo));
    setLinea(v, linea);
  }
  /**
   * Devuelve el valor que hay en el campo
   * mandado por el numero de Linea y Numero de Campo (Columna).
   * La diferencia getValor* es que devuelve el valor de los campos CTextField,
   * CComboBox, etc, excepto si estan disabled que los coge del Grid directamente.
   * @param linea int Numero de Linea
   * @param nCampo int Numero de Campo (Columna)
   * @return Object
   */
  private Object actValGrid(int linea, int nCampo)
  {
    CCheckBox chT;
    if (tCampo.get(nCampo).toString().compareTo("T") == 0)
    {
      if (! ( (CTextField) campos.get(nCampo)).isEnabled() || ! ( (CTextField) campos.get(nCampo)).isEditable())
        return getValString(linea, nCampo); // No esta enabled o editable. Devuelvo el valor del grid
      if ( ( (CTextField) campos.get(nCampo)).getTipoCampo() ==  Types.DATE)
        return ( (CTextField) campos.get(nCampo)).getFecha();
      else
        return ( (CTextField) campos.get(nCampo)).getText();
    }
    if (tCampo.get(nCampo).toString().compareTo("L") == 0)
    {
      if (! ( (CLinkBox) campos.get(nCampo)).isEnabled() || ! ( (CLinkBox) campos.get(nCampo)).isEditable())
        return getValString(linea, nCampo); // No esta enabled o editable. Devuelvo el valor del grid
      return ( (CLinkBox) campos.get(nCampo)).getText();
    }
    if (tCampo.get(nCampo).toString().compareTo("B") == 0)
    {
      chT = ( (CCheckBox) campos.get(nCampo));
      if (!chT.isEnabled())
        return "N";
      if (Formato[nCampo].equals("") || Formato[nCampo].charAt(0) != 'B')
      {
        return chT.isSelected() ? chT.getStringSelect() : chT.getStringNoSelect();
      }
      else
        return Boolean.valueOf(chT.isSelected());
    }
    if (tCampo.get(nCampo).toString().compareTo("C") == 0)
    {
      if (! ( (CComboBox) campos.get(nCampo)).isEnabled() || ! ( (CComboBox) campos.get(nCampo)).isEditable())
        return getValString(linea, nCampo); // No esta enabled o editable. Devuelvo el valor del grid
      return ( (CComboBox) campos.get(nCampo)).getText();
    }
    if (tCampo.get(nCampo).toString().compareTo("b") == 0 )
    {
      if (! ( (CButton) campos.get(nCampo)).isEnabled())
           return getValString(linea, nCampo);
      return ( (CButton) campos.get(nCampo)).getText();
    }

    return "";
  }

  /**
   * Pasa los valores del grid a los campos editables (CEditable)
   */
  private void ponValores()
  {
    ponValores(getSelectedRow(), true, false);
  }
  /**
   * Pasa los valores del grid a los campos editables (CEditable)
   * @param linea int Numero de linea del grid a pasar.
   */
  public void ponValores(int linea)
  {
    ponValores(linea, false, true);
  }
  /**
   * Pone los valores del grid a los Campos editables (CTextField, etc.)
   * @param linea Linea a la q poner los valores
   * @param foc boolean si true NO Poner el valor si el compenente tiene el foco
   * @param salir boolean si true NO Pone el valor si n es igual a la columna selecionada.
   */
  public void ponValores(int linea, boolean foc, boolean salir)
  {
    if (tCampo==null || tCampo.isEmpty())
        return;
    if (TABLAVACIA)
    {
      for (int n = 0; n < nCol; n++)
      {
        if (tCampo.get(n).toString().compareTo("T") == 0)
        {
            if (getQuery())
              ( (CTextField) campos.get(n)).resetTexto();
            else
              ( (CTextField) campos.get(n)).setText(vCampo.get(n).toString());
        }
        if (tCampo.get(n).toString().compareTo("P") == 0)
          ( (CEditable) campos.get(n)).setText(vCampo.get(n).toString());
        if (tCampo.get(n).toString().equals("B"))
        {
          if (Formato[n].equals("") || Formato[n].charAt(0) != 'B')
            ( (CCheckBox) campos.get(n)).setSelecion(vCampo.get(n).toString());
          else
            ( (CCheckBox) campos.get(n)).setSelected( ( (Boolean) vCampo.get(n)).booleanValue());
        }
        if (tCampo.get(n).toString().compareTo("C") == 0)
          ( (CComboBox) campos.get(n)).setValor(vCampo.get(n).toString());
        if (tCampo.get(n).toString().compareTo("L") == 0)
          ( (CLinkBox) campos.get(n)).setText(vCampo.get(n).toString());
        if (tCampo.get(n).toString().compareTo("b") == 0)
          ( (CButton) campos.get(n)).setText(vCampo.get(n).toString());
      }
    }
    else
    {
      for (int n = 0; n < nCol; n++)
        ponValores1(n, linea, foc, salir);
    }
  }
  /**
   * Pone a los campos (CTextField,etc) los valores del GRID.
   * @deprecated usar actualizarGrid()
   */
  public void salirFoco()
  {
    actualizarGrid();
  }
  /**
   * Pasa a los componentes editables (CTextField,CCombBox, etc) los calores actuales del Grid.
   * Usar cuando se hayan modificado externamente los campos del grid (setValor) y se
   * desee sincronizar con los valores de los componentes
   */
  public void actualizarGrid()
  {
     actualizarGrid(getSelectedRow());  
  }
   /**
   * Pasa a los componentes editables (CTextField,CCombBox, etc) los calores de la linea madanda.
   * 
   * Usar cuando se hayan modificado externamente los campos del grid (setValor) y se
   * desee sincronizar con los valores de los componentes
   */
  public void actualizarGrid(int row)
  {
       ponValores(row, false, false);
  }
  /**
   * Pone a los campos Ctextfield, etc, los valores de la linea mandada
   * como parametro
   * @deprecated
   * @param row Linea 
   */
  public void salirFoco(int row)
  {
    ponValores(row, false, false);
  }
  /**
   * Ejecutar cuando se salga del grid.
   * (en ej_edit, por ejemplo)
   * Pasa los campos de los campos editables al grid
   */
  public void salirGrid()
  {
     // Paro la edicion si el grid esta enabled
    if (isEnabled())
    {
      if (tableView.isEditing())
        tableView.editingStopped(new javax.swing.event.ChangeEvent(tableView));
    }
    procesaAllFoco(tableView.getSelectedRow());
  }

  /**
   * Pone a los campos (CTextField,etc) los valores del GRID.
   * @param n int Columna del grid de donde coger el valor
   * @param linea int Linea del grid de donde coger el valor
   * @param foc boolean si true NO Poner el valor si el compenente tiene el foco
   * @param salir boolean si true NO Pone el valor si n es igual a la columna selecionada.
   */
  public void ponValores1(int n, int linea, boolean foc, boolean salir)
  {
    if (getValString(n) == null)
      return;
    if (this.getSelectedColumn() == n && salir)
      return;
    if ( ( (Component) campos.get(n)).isEnabled() == false && !isPonValoresEnabled())
      return;
    if ( ( (Component) campos.get(n)).hasFocus() && foc)
      return;
    if (tCampo.get(n).toString().compareTo("P") == 0)
    {
      ( (CEditable) campos.get(n)).setText(getValString(linea, n).trim());
    }
    if (tCampo.get(n).toString().compareTo("T") == 0)
    {
      if ( ( (CTextField) campos.get(n)).getTipoCampo() == Types.DECIMAL)
      {
        ( (CTextField) campos.get(n)).setText(getValString(linea, n, true));
      }
      else
        ( (CEditable) campos.get(n)).setText(getValString(linea, n));
    }

    if (tCampo.get(n).toString().compareTo("B") == 0)
    {
      if (Formato[n].equals("") || Formato[n].charAt(0) != 'B')
        ( (CCheckBox) campos.get(n)).setSelecion(getValString(linea, n));
      else

//       if (((CCheckBox) campos.get(n)).isSelected())
        ( (CCheckBox) campos.get(n)).setSelected(getValBoolean(linea, n));
    }
    if (tCampo.get(n).toString().compareTo("C") == 0)
      ( (CComboBox) campos.get(n)).setValor(getValString(linea, n));
    if (tCampo.get(n).toString().compareTo("L") == 0)
      ( (CLinkBox) campos.get(n)).setText(getValString(linea, n));
    if (tCampo.get(n).toString().compareTo("b") == 0)
      ( (CButton) campos.get(n)).setText(getValString(linea, n));

  }

  public void procesaTecla(KeyEvent e, CEditable comp, int columna)
  {
    JTable table = tableView;
    int row = table.getSelectedRow();
    int col = table.getSelectedColumn();
    if (e.isControlDown() || e.isShiftDown())
      return;

    CTextField tf = null;
    boolean isLkBox = false;
    try
    {
      if (Class.forName("gnu.chu.controles.CLinkBox").isAssignableFrom(comp.
          getClass()))
      {
        tf = ( (CLinkBox) comp).texto;
        isLkBox = true;
      }
      if (Class.forName("gnu.chu.controles.CTextField").isAssignableFrom(comp.
          getClass()))
        tf = (CTextField) comp;
    }
    catch (Exception k)
    {}
    int rw;
    char tecla = e.getKeyChar();
    switch (e.getKeyCode())
    {
      case KeyEvent.VK_RIGHT:
        procesaTeclaRigth(tf, table, col, row);
        return;
      case KeyEvent.VK_LEFT:
        procesaTeclaLeft(tf, table, col, row);
        return;
      case KeyEvent.VK_DOWN: // Baja de Linea
 //       if (!isLkBox)
          procesaTeclaDown(e, columna);
        break;
      case KeyEvent.VK_UP: //Sube
        e.consume();
        if (getSelectedRow() == 0)
          return;
        if (tCampo.get(columna).equals("T"))
          this.setValor(( (CTextField) campos.get(columna)).getText(),columna);
        cambiaColumna0(getSelectedColumn(),getSelectedColumn());
        ponValores(getSelectedRow());
        if ( (col = cambiaLinea1(getSelectedRow(), getSelectedColumn())) >= 0)
        {
          if (col != getSelectedColumn())
          {
            eatCambioCol++;
            requestFocus(getSelectedRow(), col);
            return;
          }
          else
          {
//           eatCambioCol++;
            requestFocus(getSelectedRow(), col);
            return;
          }
        }
        setEatCambioLinea(getEatCambioLinea() + 1);

        requestFocus(getSelectedRow() - 1, getSelectedColumn());
        ponValores(getSelectedRow());
        afterCambiaLinea0();
        if (tCampo.get(columna).equals("T"))
          ( (CTextField) campos.get(columna)).selectAll();
        break;
      case KeyEvent.VK_F8: // Borrar Linea Activa.
           SwingUtilities.invokeLater( new Thread()
            {
              public void run()
              {
                Bborra.doClick();
              }
            });
        break;
      case KeyEvent.VK_F7: // Insertar Nueva Linea
            SwingUtilities.invokeLater( new Thread()
            {
              public void run()
              {
                 Binser.doClick();
              }
            });
    }
  }

  void procesaTeclaDown(KeyEvent e, int columna)
  {
    e.consume();
    if (tCampo.get(columna).equals("T"))
      ( (CTextField) campos.get(columna)).leePesoBasc();
    if (tCampo.get(columna).equals("T"))
      this.setValor(( (CTextField) campos.get(columna)).getText(),columna);

//    if (tCampo.get(nColuT).equals("T"))
//      this.setValor(( (CTextField) campos.get(nColuT)).getText(),columna);
    mueveSigLinea(columna, true);
    if (tCampo.get(columna).equals("T"))
      ( (CTextField) campos.get(columna)).selectAll();
  }

  void procesaTeclaRigth(CTextField tf, JTable table, int col, int row)
  {
    if (tf != null)
    {
      if (tf.getCaretPosition() != tf.getTextSuper().length())
        return;
    }
    if (table.getSelectedColumn() == getUltColAct())
    {
      mueveSigLinea(colIni);
      return;
    }
    cambiaColumna0(col, row);
    col = getProxColEdi(getSelectedColumn());// table.getSelectedColumn() + 1;
    requestFocus(row, col);
  }

  void procesaTeclaLeft(CTextField tf, JTable table, int col, int row)
  {
    if (tf == null)
    {
      procesaTeclaLeft1(table, col, row);
      return;
    }
    if (tf.getCaretPosition() == 0 ||
        (tf.getSelectionStart() == 0 &&
         tf.getSelectionEnd() == tf.getTextSuper().length()))
    {
      procesaTeclaLeft1(table, col, row);
    }
  }

  void procesaTeclaLeft1(JTable table, int col, int row)
  {
    if (table.getSelectedColumn() == colIni)
    { // Subir de Columna
      if (getSelectedRow() > 0)
        procCambiaLinea(getSelectedRow(), getSelectedRow() - 1, col,
                        colFin);
      return;
    }
    int colnueva1 = getAntColEdit(getSelectedColumn());
    cambiaColumna0(col, colnueva1,getSelectedRow());
    col=colnueva1;
    requestFocus(row, col);
  }

  private void afterCambiaLinea0()
  {
    resetCambio();
    afterCambiaLinea();
  }

  public int cambiaLinea1(int row, int col)
  {
    int n = 0;
    for (n = 0; n < nCol; n++)
    {
      if (tCampo.get(n).toString().compareTo("T") == 0)
      { // Si el Campo es TextField y tiene Error y esta enabled y Editable
        if (( (CTextField) campos.get(n)).isEnabled() && ( (CTextField) campos.get(n)).isEditable())
        ( (CTextField) campos.get(n)).procesaSalir();
        if ( ( (CTextField) campos.get(n)).getError() &&
            ( (CTextField) campos.get(n)).isEnabled() &&
            ( (CTextField) campos.get(n)).isEditable())
        {
         ( (CTextField) campos.get(n)).getMsgError();
          return n;
        }
      }
    }

    n = cambiaLinea(row, col);
    return n;
  }

  void cambiaColumna0(int col,int colNueva)
  {
    cambiaColumna0(col,colNueva, getSelectedRow());
  }

  private void cambiaColumna0(int col, int colNueva,int row)
  {
    if (col < 0)
        return;
    if (tCampo.get(col).toString().equals("L"))
    {
      ( (CLinkBox) campos.get(col)).setText( ( (CLinkBox) campos.
          get(col)).getText());
      this.setValor( ( (CLinkBox) campos.get(col)).getText() + " - " +
                    ( (CLinkBox) campos.get(col)).getTextCombo(), row,
                    col);
    }
    cambiaColumna(col, colNueva,row);
  }
  /**
   * Funcion a Machacar si se quiere controlar algo cuando se cambia una columna
   * 
   * @param col int
   * @param colNueva int
   * @param row int
   */
    @Override
  protected void cambiaColumna(int col,int colNueva,int row)
   {
     GridEvent grEvent=new GridEvent(this);
     grEvent.setColumna(col);
     grEvent.setLinea(row);
     grEvent.setColNueva(colNueva);

     processGridEvent(grEvent, true);
     afterCambiaColumna(col,colNueva,row);
   }
   protected void cambiaColumna(int col,int row)
   {
       cambiaColumna(col,col,row);
   }
   /**
    * Resetea el cambio de todas las variables del Grid
    */
    @Override
   public void resetCambio()
   {
     if (campos==null)
       return;
     int nCol = campos.size();
     for (int n = 0; n < nCol; n++)
     {
       if (campos.get(n)==null)
         continue;
       ( (CEditable) campos.get(n)).resetCambio();
     }
   }

   /**
    * Resetea el cambio de todas las variables del Grid
    * @return true si ha habido cambios en el grid
    */
    @Override
   public boolean hasCambio()
   {
     if (campos==null)
       return false;
     int nCol = campos.size();
     for (int n = 0; n < nCol; n++)
     {
       if (campos.get(n)==null)
         continue;

       if ( ( (CEditable) campos.get(n)).hasCambio())
         return true;
     }
     return false;
   }

  /**
   * Machacar esta funcion si quiere controlar algo una vez
   * que ya se ha cambiado la linea
   * Los Component mandados en setCampos estaran ya actualizados al valor
   * de la linea actual del grid.
   * @see addGridListener
   */
  public void afterCambiaLinea()
  {
  }

  public boolean isCampo(Component c)
  {
    for (int n = 0; n < campos.size(); n++)
    {
      if (c == campos.get(n))
        return true;
    }
    return false;
  }

    @Override
  void addDefaultRow()
  {
    if (vCampo == null)
    {
      super.addDefaultRow();
      return;
    }
    ArrayList v1 = new ArrayList();
    insLinea(v1);
    datosModelo.addRow(new Vector(v1));
  }

  /**
   * Machacar esta funcion si quiere controlar algo Cuando se cambie la linea
   * Es llamada cuando el grid esta disabled. Los componentes tendran el valor
   * de la ultima linea activa.
   * @parm nRow numero de linea a procesar
   */
  public void afterCambiaLineaDis(int nRow)
  {
     GridEvent grEvent=new GridEvent(this);
     grEvent.setColumna(getSelectedColumnDisab());
     grEvent.setLinea(nRow);
     grEvent.setColNueva(getSelectedColumnDisab());
      for (int i = 0; i < grListener.size(); i++)
          ( (GridListener) grListener.get(i)).afterCambiaLineaDis(grEvent);
  }

  /**
   * Esta rutina sera llamada, cada vez que se cambia la linea.
   * Si retorna >=0, NO SE CAMBIARA y se saltara al Campo devuelto.
   * <bold>El primer campo es 0</bold>
   * Sustituirla en la definición del grid para realizar alguna acción
   * cuando se cambie de linea.
   * @return campo al que ir por error. < 0 si todo ha ido bien
   * @see addGridListener
   */

  public int cambiaLinea(int row, int col)
  {
     GridEvent grEvent=new GridEvent(this);
     grEvent.setColumna(col);
     grEvent.setLinea(row);
     grEvent.setColNueva(col);
     processGridEvent(grEvent,false);
     return grEvent.getColError();
  }

  public void mueveSigLinea()
  {
    mueveSigLinea(getSelectedColumn());
  }

  public void mueveSigLinea(int columna)
  {
    mueveSigLinea(columna, true);
  }

  void mueveSigLinea(int columna, boolean focus)
  {
    boolean swInsLinea = false;
    int row = getSelectedRow();
    int rw = tableView.getSelectedRow() + 1;
    if (rw >= tableView.getRowCount())
    { // Tengo q insertar una nueva linea.
      if (TABLAVACIA)
        TABLAVACIA = false;
      cambiaColumna0(tableView.getSelectedColumn(),tableView.getSelectedColumn(), row);
      ponValores(getSelectedRow(), focus, false);
      binsert = false;
      if (!insertaLinea1(getSelectedRow(), columna))
      { // Anulado
        requestFocus(tableView.getSelectedRow(), columna);
        return;
      }

      if ( (nColErr = cambiaLinea1(getSelectedRow(), columna)) >= 0)
      { // Anulado
        requestFocus(tableView.getSelectedRow(), nColErr);
        return;
      }
      setAntRow(rw);
      swInsLinea = true;
//      if (TABLAVACIA)
//      {
//        Vector v = datosLinea();
//        addLinea(v);
//      }
      ArrayList v = new ArrayList();
      insLinea(v);
      addLinea(v);
      requestFocus(rw, colNueva); // colIni
      ponValores(rw, false, false);

      if (swInsLinea)
        afterInsertaLinea0(false);
      if (!procInsLinea)
        return;
      afterCambiaLinea0();
      SwingUtilities.invokeLater(new Thread()
      {
        public void run()
        {
          try
          {
            Thread.sleep(100);
          }
          catch (Exception k)
          {}
          ( (Component) campos.get(colNueva)).requestFocus();
        }
      });
      return;
    }
    else
      procCambiaLinea(row, row + 1, getSelectedColumn(), columna);
  }

  boolean procCambiaLinea(int rowAnt, int rowNueva, int colAnt, int colNueva)
  {
    cambiaColumna0(colAnt,colNueva, rowAnt);
    ponValores(rowAnt);
    if ( (nColErr = cambiaLinea1(rowAnt, colAnt)) >= 0)
    { // Me dicen que no cambie de Linea.
      if (nColErr != colAnt)
        eatCambioCol++;
      requestFocus(rowAnt, nColErr);
      return false;
    }
        setEatCambioLinea(1);
    if (colAnt != colNueva)
      eatCambioCol++;
    requestFocus(rowNueva, colNueva);
    ponValores(rowNueva, false, false);
    afterCambiaLinea0();
    return true;
  }
  /**
   * Devuelve un ArrayList con la linea x defecto a insertar.
   * @return 
   */
  public ArrayList getLineaDefecto()
  {
      ArrayList aa=new ArrayList();
      insLinea(aa);
      return aa;
  }
  /**
   * Llena el Vector mandado con los valores por defecto para una Nueva Linea
   */
  private void insLinea(ArrayList v)
  {
    for (int n = 0; n < nCol; n++)
    {
      if (tCampo.get(n).toString().compareTo("T") == 0 ||
          tCampo.get(n).toString().compareTo("P") == 0) // Texto o Editable
        v.add(vCampo.get(n));
      if (tCampo.get(n).toString().compareTo("B") == 0)
         v.add(vCampo.get(n));
      if (tCampo.get(n).toString().compareTo("C") == 0) // Combo
        v.add(vCampo.get(n));
      if (tCampo.get(n).toString().compareTo("L") == 0) // CLinkBox
        v.add(vCampo.get(n));
      if (tCampo.get(n).toString().compareTo("b") == 0) // Button
        v.add(vCampo.get(n));
    }
  }

  public void setDefaultValor(int col,String valor)
  {
    vCampo.set(col,valor);
  }
  /**
   * Machacar esta función si se quiere controlar algo despues de Insertar una linea
   * insLinea = true se ha insertado una linea con F7 o el boton
   *          = false es una nueva linea al final del grid
   */
  public void afterInsertaLinea(boolean insLinea)
  {

  }

  /**
   * Machacar esta funcion si se quiere controlar algo despues de Insertar una linea
   * insLinea = true se ha insertado una linea con F7 o el boton
   *          = false es una nueva linea al final del grid
   */
  private void afterInsertaLinea0(boolean insLinea)
  {
    procInsLinea = true;
    afterInsertaLinea(insLinea);
  }

  /**
   * Si es llamado con false no se llamara a afterCambiaLinea despues de Insertar
   * una nueva linea, en caso contrario si.
   * Esta funcion debe ser llamada desde un AfterInsertaLinea
   * ya que afterInsertaLinea0 la llama con false.
   *
   * @param procInsLin si se debe llamar a aftercambialinea
   */
  public void setProcInsLinea(boolean procInsLin)
  {
    procInsLinea = procInsLin;
  }
/**
 * Funcion a machacar cuando se quiera hacer algo despues de borrar linea
 *
 */
  public void afterDeleteLinea()
  {
     GridEvent grEvent=new GridEvent(this);
     grEvent.setColumna(getSelectedColumn());
     grEvent.setLinea(getSelectedRow());
      for (int i = 0; i < grListener.size(); i++)
          ( (GridListener) grListener.get(i)).afterDeleteLinea(grEvent);
  }

  Vector datosLinea()
  {
    CCheckBox chT;
    Vector v = new Vector();
    for (int nCampo = 0; nCampo < nCol; nCampo++)
    {
      if (tCampo.get(nCampo).toString().compareTo("T") == 0 ||
          tCampo.get(nCampo).toString().compareTo("P") == 0)
        v.addElement( ( (CEditable) campos.get(nCampo)).getText());
      if (tCampo.get(nCampo).toString().compareTo("B") == 0)
      {
        if (Formato[nCampo].equals("") || Formato[nCampo].charAt(0) != 'B')
        {
          chT = ( (CCheckBox) campos.get(nCampo));
          v.addElement(chT.isSelected() ? chT.getStringNoSelect() :
                       chT.getStringNoSelect());
        }
        else
          v.addElement(( (CCheckBox) campos.get(nCampo)).isSelected());
      }
      if (tCampo.get(nCampo).toString().compareTo("C") == 0)

//        if ( ( (CComboBox) campos.get(nCampo)).isAsignadoValorBD())
//          v.addElement( ( (CComboBox) campos.get(nCampo)).getValor());
//        else
        v.addElement( ( (CComboBox) campos.get(nCampo)).getText());

      if (tCampo.get(nCampo).toString().compareTo("L") == 0)
        v.addElement( ( (CLinkBox) campos.get(nCampo)).getText());
      if (tCampo.get(nCampo).toString().compareTo("b") == 0)
        v.addElement( ( (CButton) campos.get(nCampo)).getText());

    }
    return v;
  }

  /**
   * Esta rutina sera llamda, cada vez que se inserta la linea.
   * Si retorna false, NO SE CAMBIARA.
   *
   */
  public boolean insertaLinea1(int row, int col)
  {
     if ( getQuery())
         return false;
    for (int n = 0; n < nCol; n++)
    {
      if (tCampo.get(n).toString().compareTo("T") == 0)
      { // Si el Campo es TextField y tiene Error y esta enabled
        if ( ( (CTextField) campos.get(n)).isEnabled() && ( (CTextField) campos.get(n)).isEditable())
          ( (CTextField) campos.get(n)).procesaSalir();
        if ( ( (CTextField) campos.get(n)).getError() &&
            ( (CTextField) campos.get(n)).isEnabled() &&
            (( (CTextField) campos.get(n)).isEditable() ))
        {
          return false;
        }
      }
    }
    return insertaLinea(row, col);
  }
  /**
   * Machacar clase si se desea hacer algo antes de insertar linea
   * @param row
   * @param col
   * @return true si se puede insertar linea. false en caso contrario.
   */
  public boolean insertaLinea(int row, int col)
  {
    return canInsertLinea;
  }

  public void setCanInsertLinea(boolean insertLinea)
  {
    Binser.setEnabled(insertLinea);
    canInsertLinea = insertLinea;
  }

  public boolean getCanInsertLinea()
  {
    return canInsertLinea;
  }

  void procesaEnter(KeyEvent ke)
  {

    if (ke.isAltDown() || ke.isControlDown() || ke.isShiftDown())
      return;
    int ultColAct = getUltColAct();
    if (getSelectedColumn() == ultColAct)
    {
      procesaTab(ke);
      return;
    }
    eatCambioCol++;
    int colNueva = -1;
    colNueva = getProxColEdi(getSelectedColumn());
    cambiaColumna0(getSelectedColumn(),colNueva);
    int col = getSelectedColumn() + 1;
    requestFocus(getSelectedRow(), colNueva);
        setAntColumn(col - 1);
    procCambiaCol(null);
    ke.consume();
  }

  int getUltColAct()
  {
    return getUltColAct(getSelectedColumn());
  }

  int getUltColAct(int colActual)
  {
    int ultColAct = colActual;
    for (int n = colActual + 1; n < nCol; n++)
    {
      if ( ( (Component) campos.get(n)).isEnabled())
        ultColAct = n;
    }
    return ultColAct;
  }
  /**
   * Establece si debe realizarse un requestFocus por defecto a las columnas que no son
   * editables. Por defecto es false.
   * @param colEditables boolean true si debe hacerse un req. Focus por defecto aunque el
   * cambo no sea swGridEditable. Solo valido para TextFields
   */
  public void setReqFocusEdit(boolean ReqFocusEdit)
  {
    this.reqFocusEdit=ReqFocusEdit;
  }
  /**
   *
   * @return boolean true si se hara un request focus a las columnas con un campo no swGridEditable
   */
  public boolean getReqFocusEdit()
  {
   return reqFocusEdit;
  }

  /**
   * Devuelve la proxima columna swGridEditable
   * @param colActual int Columna a partir de la que buscar
   * @return int Proxima Columna Editable. La misma columna si no hay ninguna swGridEditable
   */
  int getProxColEdi(int colActual)
  {
    for (int n = colActual + 1; n < nCol; n++)
    {
      if ( ( (Component) campos.get(n)).isEnabled())
      {
        if (! ((JComponent) campos.get(n)).isRequestFocusEnabled())
          continue;
        if (tCampo.get(n).equals(TIPO_TEXTFIELD))
        {
          if ( ( (CTextField) campos.get(n)).isEditable() || reqFocusEdit)
            return n;
        }
       
        if (tCampo.get(n).equals(TIPO_LINKBOX))
        {
            if   (( (CLinkBox) campos.get(n)).isEditable() || reqFocusEdit)
                return n;
        }
        if (tCampo.get(n).equals(TIPO_COMBOBOX))
        {
            if   (( (CComboBox) campos.get(n)).isEditable() || reqFocusEdit)
                return n;
        }
      }
    }
    return colActual;
  }

  void procesaTab(KeyEvent ke)
  {
    int row = getSelectedRow();
    if (ke.isAltDown() || ke.isControlDown())
      return;
    eatCambioCol++;
    int colNueva;

    if (!ke.isShiftDown())
    {
      int ultColAct = getUltColAct();
      if (getSelectedColumn() == ultColAct )
      {
        cambiaColumna0(getSelectedColumn(),0);
        mueveSigLinea(0);
        if (ke.getKeyCode() == KeyEvent.VK_ENTER || ke.getKeyCode() == KeyEvent.VK_TAB)
          ke.consume();
      }
      else
      {
        ke.consume();
        colNueva=getProxColEdi(getSelectedColumn());
        cambiaColumna0(getSelectedColumn(),colNueva);
        requestFocus(getSelectedRow(), colNueva);
      }
    }
    else
    {
      if (getSelectedColumn() == colIni)
      {
        if (row > 0)
          row--;
        if (!procCambiaLinea(getSelectedRow(), row, getSelectedColumn(), colFin))
        {
          ke.consume();
          return;
        }
        setEatCambioLinea(1);
        cambiaColumna0(getSelectedColumn(),colFin);
        requestFocus(row, colFin);
        ke.consume();
      }
      else
      {
        ke.consume();
        colNueva=getAntColEdit(getSelectedColumn());
        cambiaColumna0(getSelectedColumn(),colFin);
        requestFocus(getSelectedRow(),colNueva );
      }
    }
  }

  int getAntColEdit(int col)
  {
    for (int n = col - 1; n >= 0; n--)
    {
      if ( ( (Component) campos.get(n)).isEnabled())
      {
        if (tCampo.get(n).equals("T"))
        {
          if ( ( (CTextField) campos.get(n)).isEditable() || reqFocusEdit)
            return n;
        }
        else
          return n;
      }
    }
    return col;
  }

  /**
   * Establece la Columna donde ira cuando se inserte una nueva linea.
   * por defecto es igual a colIni.
   *
   * @param colNue Columna donde ir
   */
  public void setColNueva(int colNue)
  {
    colNueva=colNue;
  }
  public int getColNueva()
  {
    return colNueva;
  }
    @Override
  public void removeAllDatos(){
      
        if (SwingUtilities.isEventDispatchThread())
        {
          removeAllDatos_(); 
        }
        else
        {
          try {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                    @Override
               public void run()
               {
                   removeAllDatos_();
               }
            });
            
          } catch (InterruptedException ex) {
            Logger.getLogger(Cgrid.class.getName()).log(Level.SEVERE, null, ex);
          } catch (InvocationTargetException ex) {
            Logger.getLogger(Cgrid.class.getName()).log(Level.SEVERE, null, ex);
          }
        }
    }
    
   void removeAllDatos_()
   {
    removeAllDatos_Cgrid();
  
    if (isPonValoresInFocus())
      ponValores();
    TABLAVACIA=true;
  }
  public void setQuery(boolean swQuery)
  {
    query=swQuery;   
    if (campos==null)
       return;
     int n1=getColumnCount();
     for (int n = 0; n < n1 ; n++)
     {
       if (campos.get(n)==null)
         continue;
       if (campos.get(n) instanceof CQuery  )
         ( (CQuery) campos.get(n)).setQuery(swQuery);
     }
  }
  public boolean getQuery() {return query;}
  public void setPonValoresInFocus(boolean ponValInFocus)
  {
    ponValoresInFocus=ponValInFocus;
  }

  public boolean getPonValoresInFocus()
  {
    return isPonValoresInFocus();
  }
  public void setPonValoresEnabled(boolean ponValorEnab)
  {
    ponValoresEnabled=ponValorEnab;
  }
  public boolean getPonValoresEnabled()
  {
    return isPonValoresEnabled();
  }

    /**
     * @return the ponValoresEnabled
     */
    public boolean isPonValoresEnabled() {
        return ponValoresEnabled;
    }

    /**
     * @return the ponValoresInFocus
     */
    public boolean isPonValoresInFocus() {
        return ponValoresInFocus;
    }

    /**
     * @return the eatCambioLinea
     */
    public int getEatCambioLinea() {
        return eatCambioLinea;
    }

    /**
     * @param eatCambioLinea the eatCambioLinea to set
     */
    public void setEatCambioLinea(int eatCambioLinea) {
        this.eatCambioLinea = eatCambioLinea;
    }

    /**
     * @return the antRow
     */
    public int getAntRow() {
        return antRow;
    }

    /**
     * @param antRow the antRow to set
     */
    public void setAntRow(int antRow) {
        this.antRow = antRow;
    }

    /**
     * @return the antColumn
     */
    public int getAntColumn() {
        return antColumn;
    }

    /**
     * @param antColumn the antColumn to set
     */
    public void setAntColumn(int antColumn) {
        this.antColumn = antColumn;
    }

}
class tfKeyListener extends KeyAdapter
{
  CEditable tf;
  int nCampo;
  JTable table;
  CGridEditable jt;
  public tfKeyListener(CEditable c,int nCampo,CGridEditable jt)
  {
    this.tf = c;
    this.nCampo = nCampo;
    this.jt=jt;
    table=jt.tableView;
  }
    @Override
  public void keyPressed(KeyEvent e)
  {
    jt.procesaTecla(e,tf,nCampo);

  }
}

class focusAdaptGrid extends FocusAdapter
{
  int nCol;
  CGridEditable padre;
  public focusAdaptGrid(CGridEditable grid, int col)
  {
    nCol=col;
    padre=grid;
  }

    @Override
  public void focusLost(java.awt.event.FocusEvent e)
  {
//    System.out.println("e: "+e.getOppositeComponent());
    if (e.getOppositeComponent()==padre.tableView || e.getOppositeComponent()==null
        || e.getOppositeComponent() == padre)
      return;
//    if (e.getOppositeComponent() instanceof JRootPane )
//    {
//          padre.cambiaColumna0(nCol,nCol);
//          return;
//    }
    for (int n=0;n<padre.nCol;n++)
    {
      if (e.getOppositeComponent()==padre.campos.get(n))
        return;
    }
    padre.cambiaColumna0(nCol,nCol);
  }
}

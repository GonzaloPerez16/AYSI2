alter table v_matadero add pai_inic varchar(2);
alter table v_saladesp add pai_inic varchar(2);
update v_matadero set pai_inic = (select pai_inic from paises where pai_codi = v_matadero.pai_codi) where pai_codi>0; --and acc_ano=2016;
update v_saladesp set pai_inic = (select pai_inic from paises where pai_codi = v_saladesp.pai_codi) where pai_codi>0; --and acc_ano=2016;
alter table v_matadero drop pai_codi;
alter table v_saladesp  drop pai_codi;

-- Para insercion tarifa
create table tarifa_origen 
(
pro_codi int,
tar_precio float
);
--
-- Incluye comision por debajo tarifa
alter table tipotari add tar_corein float;				-- Comision Repres. Inferior
update tipotari set tar_corein=0;
alter table tipotari alter tar_corein set not null;			-- Comision Repres. Inferior
--
-- 
alter table v_articulo add pro_kgmiun float; -- kg. Minimo Unidad.
alter table v_articulo add pro_kgmaun float; -- kg. Maximo Unidad.
alter table v_articulo add pro_cointa float; -- Costo a Incrementar en Tarifa
--
-- Tipos tarifas. Incluida comision representante
--
alter table tipotari add tar_comrep float;				-- Comision Repres.
update tipotari set tar_comrep=0;
alter table tipotari alter tar_comrep set not null;			-- Comision Repres.
--
-- Incluido pais en lineas inventario
--
ALTER TABLE linvproduc ADD pai_codi varchar(3); -- Pais
ALTER TABLE linvproduc ADD prp_fecpro date ; -- Fecha Prod.
-- 
-- Incluidos campos trazabilidad en stockpartidas
--
alter table stockpart add cam_codi varchar(2);
alter table stockpart add stp_fecpro date;	 -- Fecha Produccion.
alter table stockpart add stp_nucrot varchar(30); -- Numero Crotal
alter table stockpart add stp_painac char(2);	 -- Pais de Nacimiento
alter table stockpart add stp_engpai char(2) ;		-- Pais de engorde
alter table stockpart add stp_paisac char(2);		-- Pais de Sacrificio
alter table stockpart add stp_fecsac date;	-- Fecha Sacrificio
alter table stockpart add mat_codi int;		-- Matadero
alter table stockpart add sde_codi int;		-- Sala despiece
alter table stockpart add stp_traaut smallint default 1 not null;

--
-- Añadido codigo articulo equivalente
--
alter table v_articulo add pro_codequ int ;
---

ALTER TABLE cinvproduc ADD tid_codi int not null default 0; -- Tipo Despiece
create table anjelica.cinvproduc
(	
	cip_codi int not null,			-- Numero de Inventario
	usu_nomb varchar(15) not null,	-- Usuario q. realizo el inventario
	cip_fecinv date not null,		-- Fecha de Inventario
	cam_codi varchar(2) not null,	-- Codigo de Camara (Tabla v_camaras)
	alm_codi int not null, 			-- Almacen
	cip_coment varchar(100),			-- Comentario
    constraint ix_cinvproduc primary key (cip_codi)
);
create index ix_cinvproduc1 on cinvproduc(cip_fecinv);
--
-- Lineas  Inventarios  piezas producción
--
-- drop table linvproduc;
create table anjelica.linvproduc
(   
    cip_codi int not null,			-- Numero de Inventario
    lip_numlin int not null,			-- Numero de Linea
    prp_ano  int not null,      		-- Ejercicio del lote
    prp_seri char(1) not null,			-- Serie del Lote
    prp_part int not null,			-- Partida
    pro_codi int not null,			-- Producto    
    prp_indi int not null,          -- Individuo de Lote	
    prp_peso decimal(6,2) not null,		-- Peso de inventario
	prv_codi int not null,			-- Prvoveedor
	prp_fecsac date not null,		-- Fecha Sacrificio
	lip_fecalt timestamp not  null default current_timestamp,
  constraint ix_linvproduc primary key(cip_codi,lip_numlin)
);
create index ix_linvproduc2 on linvproduc(pro_codi,prp_ano,prp_part,prp_seri,prp_indi);

create view anjelica.v_invproduc as
select c.cip_codi,c.usu_nomb,cip_fecinv, c.cam_codi,c.alm_codi,c.tid_codi,c.cip_coment,lip_numlin,prp_ano, prp_seri, prp_part, l.pro_codi, a.pro_nomb,
prp_indi,prp_peso,l.prv_codi,prv_nomb,prp_fecsac,pai_codi,lip_fecalt from cinvproduc as c, linvproduc as l left join v_articulo as a on l.pro_codi=a.pro_codi
left join v_proveedo as pv on l.prv_codi = pv.prv_codi where
 c.cip_codi=l.cip_codi;
grant select on  v_invproduc to public;
--
-- Incluir en tarifa la comision de los representantes
--
alter table  tarifa add tar_comrep float default 0 not null;   -- Comision Representantes
alter table  taricli add tar_comrep float default 0 not null;   -- Comision Representantes
--
-- Añadida divisa en historico ventas
--
alter table histventas add div_codi int not null default 1;
--
-- Incluida fecha alta en v_albcompar
--
--alter table v_albcompar drop  acp_fecalt;
--alter table v_albcompar add  acp_fecalt  timestamp not null  default current_timestamp;
--
-- Incluido comision por kg. y kilos de portes en albaranes de compras
--
alter table v_albacoc add  acc_imcokg float not null default 0; 		-- Importe de Comisiones / Kg
alter table hisalcaco add  acc_imcokg float not null default 0; 		-- Importe de Comisiones / Kg
alter table v_albacoc add  acc_kilpor int ; 		-- Kg. de portes
alter table hisalcaco add  acc_kilpor int ;		-- Kg. de Portes.
update v_albacoc set acc_kilpor=0;
update hisalcaco set acc_kilpor=0;
-- 
-- Añadido numero serie a pedidos venta
alter table pedvenc add pvc_id serial;
alter table hispedvenc add pvc_id serial;
grant all on pedvenc_pvc_id_seq to public;
--
-- Incluido tipo tarifa maestro en configuracion
--
alter table configuracion add cfg_tarini smallint not null default 2;
alter table tipotari add constraint  ix_tipotari unique(tar_codi);

-- Incluido campo para controlar si producto salida es tipo Produccion
alter table v_despfin add def_tippro char(1) not null default 'N' ;
alter table desfinhis  add def_tippro char(1) not null default 'N' ;
-- Añadido campo servir a Cliente
drop view v_cliente;
alter table clientes   add cli_servir smallint default 1 not null;
create or replace view anjelica.v_cliente as select *,cli_codrut as cli_carte,cli_codrut as cli_valor from anjelica.clientes;
grant select on anjelica.v_cliente to PUBLIC;
alter table cliencamb   add cli_servir smallint default 1 not null;
--
-- Añadido campo precio valoracion
--
alter table  desproval add dpv_preval decimal(6,2);
update desproval set dpv_preval = 0;
alter table desproval alter dpv_preval set not null;
--
-- Añadida fecha confirmacion y listado
--
alter table  pedicoc add pcc_feccon date;			-- Fecha Confirmacion
alter table  pedicoc add  pcc_feclis date; -- Listado 

--
-- Clasificacion de Lomos por proveedores
--
alter table claslomos add prv_codi int not null default 0;
-- 
-- Ampliado campo NIF a 30 Caracteres
--
drop view v_cliente;
alter table clientes alter cli_nif type varchar(30);
create or replace view anjelica.v_cliente as select *,cli_codrut as cli_carte,cli_codrut as cli_valor from anjelica.clientes;
grant select on anjelica.v_cliente to PUBLIC;
--
drop view anjelica.v_albventa;
create view anjelica.v_albventa as select c.avc_id,c.emp_codi,c.avc_ano,c.avc_serie,c.avc_nume,
cli_codi,avc_clinom,avc_fecalb, usu_nomb,avc_tipfac, cli_codfa,
fvc_ano,fvc_nume,c.avc_cerra,avc_impres,avc_fecemi,sbe_codi,avc_cobrad,avc_obser,avc_fecrca,
avc_basimp,avc_kilos,avc_unid,div_codi,avc_impalb,avc_impcob,avc_dtopp,avc_dtootr,avc_valora,fvc_serie,
avc_depos,avl_numlin,pro_codi,avl_numpal,pro_nomb,avl_canti,avl_prven,avl_prbase,avc_repres,
tar_preci,avl_unid,
avl_canbru,avl_fecalt,fvl_numlin,avl_fecrli,alm_codori,alm_coddes,avl_dtolin 
from v_albavel as l, v_albavec as c 
where c.emp_codi=l.emp_codi and c.avc_ano=l.avc_ano and c.avc_serie=l.avc_serie 
and c.avc_nume=l.avc_nume;
grant select on anjelica.v_albventa to public;
--
--
-- Cambiada ruta a 5 caracteres
drop view v_cliente;
alter table clientes alter cli_codrut type varchar(5);
alter table cliencamb alter cli_codrut type varchar(5); -- Codigo cliente en rutas.
create or replace view anjelica.v_cliente as select *,cli_codrut as cli_carte,cli_codrut as cli_valor from anjelica.clientes;
grant select on anjelica.v_cliente to PUBLIC;
update clientes set cli_codrut = rut_codi || cli_codrut where cli_codrut!='';
--

alter table v_albavec add avc_rcaedi varchar(2);
--
--
-- Añadido campo Cod.Cliente reparto
--
--select cli_codi,cli_nomen,cli_poble,rut_codi,REP_CODI from clientes as cl where rut_codi ='LO'  and cli_activ='S' AND EXISTS (SELECT DISTINCT(CLI_CODI) from v_albavec as a where a.cli_codi=cl.cli_codi
-- and a.avc_fecalb>='20160606') order by cli_codi
drop view v_albdepserv;
drop view v_cliprv;
drop view v_cliente;
alter table clientes rename cli_carte  to cli_codrut; -- Codigo cliente en rutas.
alter table clientes alter cli_codrut type varchar(5); -- Codigo cliente en rutas.
alter table cliencamb rename cli_carte to cli_codrut; -- Codigo cliente en rutas.
alter table cliencamb alter cli_codrut type varchar(5); -- Codigo cliente en rutas.
update clientes set cli_codrut='' where cli_codrut = '1';
create or replace view anjelica.v_cliente as select *,cli_codrut as cli_carte,cli_codrut as cli_valor from anjelica.clientes;
grant select on anjelica.v_cliente to PUBLIC;
create view anjelica.v_albdepserv as select sa.*,ca.avc_fecalb,ca.avc_id,cl.cli_nomb,cl.cli_nomco
 from albvenserc as sa, v_albavec as ca,
clientes as cl
where ca.avc_ano=sa.avc_ano
and  ca.emp_codi = sa.emp_codi
and  ca.avc_serie= sa.avc_serie
and  ca.avc_nume= sa.avc_nume
and sa.cli_codi = cl.cli_codi;
grant select on anjelica.v_albdepserv to public;
create view anjelica.v_cliprv as 
select 'E' as tipo, cli_codi as codigo, cli_nomb as nombre from anjelica.clientes 
union all
select 'C' AS tipo,prv_codi as codigo, prv_nomb as nombre from anjelica.v_proveedo;
grant select on anjelica.v_cliprv to public;

--
-- Añadir columna representante a albaranes venta
--
alter table v_albavec rename avc_rcaedi to avc_repres; -- Representante
update v_albavec set avc_repres = (select  rep_codi from clientes as c where c.cli_codi=v_albavec.cli_codi);
update v_albavec set avc_repres = 'MA' where avc_repres is null;
alter table v_albavec alter avc_repres  set not null; -- Representante

--
-- Añado precio original para articulo
alter table  desproval add dpv_preori decimal(6,2);
update desproval set dpv_preori = 0;
alter table desproval alter dpv_preori set not null;
-- Añadido campos de posicion lote y peso del codigo barras prv.
alter table v_proveedo add prv_poloin smallint;	-- Posicion inicial Lote en cod.Barras interno prv
alter table v_proveedo add prv_polofi smallint;	-- Posicion final Lote en cod.Barras interno prv
alter table v_proveedo add prv_popein smallint;	-- Posicion inicial peso en cod.Barras interno prv
alter table v_proveedo add prv_popefi smallint;	-- Posicion final peso en cod.Barras interno prv
--
-- Cambiados paises a iniciales
--
alter table v_albcompar alter opcional1  type varchar(2);
alter table v_albcompar alter opcional2  type varchar(2);
alter table v_albcompar alter clasificacion  type varchar(2);
drop trigger albcompar_update on anjelica.v_albcompar;
update v_albcompar set opcional1 = (select pai_inic from paises where pai_codi = v_albcompar.acp_paisac) where acp_paisac>0; --and acc_ano=2016;
update v_albcompar set opcional2 = (select pai_inic from paises where pai_codi = v_albcompar.acp_painac) where acp_painac>0; -- and acc_ano=2016;
update v_albcompar set clasificacion = (select pai_inic from paises where pai_codi = v_albcompar.acp_engpai) where acp_engpai>0; -- and acc_ano=2016;
alter table v_albcompar rename acp_paisac to acp_paisac1;
alter table v_albcompar rename acp_painac to acp_painac1;
alter table v_albcompar rename acp_engpai to acp_paieng1;
alter table v_albcompar rename opcional1 to acp_paisac;
alter table v_albcompar rename opcional2 to acp_painac;
alter table v_albcompar rename clasificacion to acp_engpai;
create trigger albcompar_update BEFORE UPDATE OR DELETE  on anjelica.v_albcompar for each row execute procedure anjelica.fn_mvtoalm();

-- Historico Compras
alter table hisalpaco add opcional1  varchar(2);
alter table hisalpaco add opcional2   varchar(2);
alter table hisalpaco alter clasificacion  type varchar(2);

update hisalpaco set opcional1 = (select pai_inic from paises where pai_codi = hisalpaco.acp_paisac) where acp_paisac>0 ;

update hisalpaco set opcional2 = (select pai_inic from paises where pai_codi = hisalpaco.acp_painac) where acp_painac>0; 

update hisalpaco set clasificacion = (select pai_inic from paises where pai_codi = hisalpaco.acp_engpai) where acp_engpai>0 ;

alter table hisalpaco rename acp_paisac to acp_paisac1;
alter table hisalpaco rename acp_painac to acp_painac1;
alter table hisalpaco rename acp_engpai to acp_paieng1;
alter table hisalpaco rename opcional1 to acp_paisac;
alter table hisalpaco rename opcional2 to acp_painac;
alter table hisalpaco rename clasificacion to acp_engpai;

-- 
-- Incluir Camara en Stock-partidas 
--
alter table  stockpart add cam_codi varchar(2);
--
-- Incluir ruta y fecha preparacion en Pedidos ventas
---
alter table pedvenc add rut_codi varchar(2);  
alter table hispedvenc add rut_codi varchar(2) ;
alter table pedvenc add pvc_fecpre date; 
alter table hispedvenc add pvc_fecpre date;
--
-- Empresa
alter table empresa rename emp_empcon to emp_loclcc;
update  empresa set emp_loclcc= 9;
alter table empresa alter emp_loclcc set not null;
drop view v_empresa;
CREATE OR REPLACE VIEW v_empresa AS 
 SELECT empresa.emp_codi,
    empresa.emp_nomb,
    empresa.emp_dire,
    empresa.emp_pobl,
    empresa.emp_codpo,
    empresa.emp_telef,
    empresa.emp_fax,
    empresa.emp_nif,
    empresa.emp_loclcc,
    empresa.emp_nurgsa,
    empresa.emp_nomsoc,
    empresa.pai_codi,
    empresa.emp_orgcon,
    empresa.emp_cercal,
    empresa.emp_labcal,
    empresa.emp_obsfra,
    empresa.emp_obsalb,
    empresa.emp_vetnom,
    empresa.emp_vetnum,
    empresa.emp_numexp,
    empresa.emp_codcom,
    empresa.emp_codpvi,
    empresa.emp_divimp,
    empresa.emp_divexp,
    empresa.emp_desspr,
    empresa.emp_codedi,
    empresa.emp_regmer,
    empresa.emp_dirweb,
    prov_espana.cop_nombre AS emp_nomprv
   FROM empresa
     LEFT JOIN prov_espana ON "substring"(empresa.emp_codpo::text, 1, 2) = prov_espana.cop_codi::text;
 grant select on anjelica.v_empresa to public;
    -- Longitud cuenta contable cliente
drop view anjelica.v_coninvent;
alter table anjelica.coninvlin add lci_numcaj smallint not null default 0;	-- Numero Caja
create view anjelica.v_coninvent as
select c.emp_codi,c.cci_codi,c.usu_nomb,cci_feccon, cam_codi,alm_codi,lci_nume,prp_ano, prp_empcod, prp_seri, prp_part, pro_codi, pro_nomb,
prp_indi,lci_peso,lci_kgsord,lci_numind,lci_regaut,lci_coment,lci_numcaj,lci_numpal,alm from coninvcab as c, coninvlin as l where
c.emp_codi=c.emp_codi
and c.cci_codi=l.cci_codi;
--
alter table usuarios add usu_clanum smallint not null default 0; -- Password Corta de usuario (sin encriptar)
--alter table usuarios add constraint ix_passnum unique(usu_pasnum);

alter table desporig add cli_codi int; -- Cliente para el que se hace el despiece
alter table deorcahis add cli_codi int; -- Cliente para el que se hace el despiece
--
alter table pedvenc add pvc_depos char(1) not null default 'N'; -- Deposito ?alter table pedvenc add
alter table hispedvenc add pvc_depos char(1) not null default 'N';

-- 
alter table pedvenc add pvc_incfra char(1) not null default 'N'; -- Incluir Fra?
alter table hispedvenc add pvc_incfra char(1) not null default 'N'; -- Incluir Fra?
alter table pedvenc add pvc_comrep varchar(200);
alter table hispedvenc add pvc_comrep varchar(200);
drop view v_pedven;
create or replace view anjelica.v_pedven as select  c.emp_codi,c.eje_nume, c.pvc_nume , cli_codi , alm_codi, pvc_fecped,
 pvc_fecent, pvc_comen , pvc_comrep, pvc_confir , avc_ano , avc_serie , avc_nume ,
 c.usu_nomb , pvc_cerra , pvc_nupecl , pvc_impres ,pvc_depos,
 l.pvl_numlin, pvl_kilos,pvl_canti,pvm_canti,pvm_coment,pvl_unid,pvl_tipo, l.pro_codi, ar.pro_nomb as pvl_nomart,ar.pro_codart,pve_nomb,
 pvl_comen, pvl_precio ,pvl_precon ,prv_codi,pvl_feccad, pvl_fecped, pvl_fecmod,m.usu_nomb as pvm_usunom,pvm_fecha  from 
 pedvenc as c, pedvenl as l  left join pedvenmod as m ON
 m.emp_codi=l.emp_codi
 and m.eje_nume= l.eje_nume and m.pvc_nume=l.pvc_nume
 and m.pvl_numlin=l.pvl_numlin, v_articulo as ar left join prodventa on pro_codart = pve_codi
 where c.emp_codi=l.emp_codi 
 and c.eje_nume=l.eje_nume and c.pvc_nume = l.pvc_nume 
 and l.pro_codi = ar.pro_codi;
grant select on anjelica.v_pedven to public;
-- Clasificicacion Pieza.
alter table v_albcompar rename column  tipo_c1 to acp_clasi;
alter table hisali add column  acp_clasi varchar(10);

alter table tarifa alter pro_codart type varchar(15);
alter table v_articulo alter pro_codart type varchar(15);

alter table anjelica.tipodesp add tid_merven smallint not null default 0 ;-- AutoMer

alter table anjelica.albrutalin add cli_nomen varchar(50) ;-- Nombre Cliente 
alter table anjelica.albrutalin add	cli_diree varchar(100) ; -- Direccion entrega
alter table anjelica.albrutalin add cli_poble varchar(50) ; -- Poblacion entrega
alter table anjelica.albrutalin add	cli_codpoe varchar(8); -- Cod. Postal Envio
drop view anjelica.v_albruta;
create or replace view v_albruta as select c.*,l.alr_orden,l.avc_id,alr_bultos,alr_palets,
alr_unid,alr_kilos,alr_horrep,alr_comrep,cli_nomen,cli_diree,cli_poble,cli_codpoe,alr_repet,
al.emp_codi,al.avc_ano,al.avc_serie,al.avc_nume,al.cli_codi,al.avc_clinom,al.avc_kilos,
al.avc_unid 
from anjelica.albrutacab as c, anjelica.albrutalin as l,anjelica.v_albavec as al 
where c.alr_nume=l.alr_nume and al.avc_id = l.avc_id;
grant select on v_albruta to public;
--
update anjelica.v_albavec set cli_ruta=0;
alter table anjelica.v_albavec alter cli_ruta type varchar(2);
update anjelica.hisalcave set cli_ruta=0;
alter table anjelica.hisalcave alter cli_ruta type varchar(2);
update anjelica.hisalbavec set cli_ruta=0;
alter table anjelica.hisalbavec alter cli_ruta type varchar(2);

-- Incluido ID en albaran de compras
alter table v_albacoc add acc_id serial not null;
alter table hisalcaco add acc_id int;

-- Incluida imagen de logo en tipos listado.
alter table anjelica.listados add lis_logo varchar(50);
-- Locale de Pais
alter table anjelica.paises drop loc_codi;
alter table anjelica.paises add loc_codi varchar(5) not null default 'es_ES';

-- drop table anjelica.locales;
create table anjelica.locales
(
loc_codi varchar(5) not null, -- Codigo de Locale.
loc_nomb varchar(30) not null, -- Nombre de Locale
primary key (loc_codi)
);
insert into anjelica.locales values('es_ES','Español (España)');
alter table anjelica.paises add constraint loc_codi foreign key (loc_codi)
    references anjelica.locales(loc_codi);
--- 
-- Nombres de Articulos en diferentes Idiomas
--
--drop table anjelica.articulo_locale
create table anjelica.articulo_locale
(
pro_codi int not null,
loc_codi varchar(5) not null,
pro_nomloc varchar(40) not null
);


-- Cambiado codigo postal de empresa y tabla prov_espana a varchar(8)
 drop view anjelica.v_empresa;
 alter table anjelica.empresa alter emp_codpo type varchar(8);
 alter table anjelica.prov_espana alter cop_codi type varchar(2);
 CREATE VIEW anjelica.v_empresa as select emp_codi,emp_nomb ,	 -- Nombre de Empresa
 emp_dire,	 -- Direccion Empresa
 emp_pobl,	 -- Poblacion Empresa
 emp_codpo ,	    	 -- Codigo Postal
 emp_telef ,	 -- Telefono
 emp_fax ,	 -- FAX
 emp_nif ,	 -- NIF
 emp_nurgsa , -- Codigo Numero Registro Sanitario
 emp_nomsoc , -- Nombre Social
 pai_codi ,	    	 -- Pais por defecto
 emp_orgcon , -- Organismo de Control.
 emp_cercal , -- Certificacion Calidad
 emp_labcal , -- Etiqueta Calidad
 emp_obsfra ,--Observaciones Factura
 emp_obsalb ,--Observaciones Albaran
 emp_vetnom , --Nombre Veterinario
 emp_vetnum , --Numero Veterinario
 emp_numexp , --Numero Explotacion
 emp_codcom ,         -- Comunidad de Empresa
 emp_codpvi ,    	 -- Provincia de Empresa
 emp_divimp ,         -- Divisa de Importacion
 emp_divexp ,         -- Divisa de Exportación
 emp_desspr , -- Destino Subproductos
 emp_codedi , -- Codigo EDI
 emp_regmer , -- Registro Mercantil
 emp_dirweb , -- Direccion web de la empresa 
 cop_nombre as emp_nomprv
 from anjelica.empresa left join  anjelica.prov_espana on substring(emp_codpo from 1 for 2)  = cop_codi;
 grant select on anjelica.v_empresa to public;
-- Cambiado codigo postal cliente a varchar(8)
drop view anjelica.v_cliprv;
drop view anjelica.v_cliente;
alter table anjelica.clientes alter cli_codpo type varchar(8);
alter table anjelica.clientes alter cli_codpoe type varchar(8);
alter table anjelica.cliencamb alter cli_codpo type varchar(8);
alter table anjelica.cliencamb alter cli_codpoe type varchar(8);
create or replace view anjelica.v_cliente as select *,rut_codi as cli_valor from anjelica.clientes;
grant select on anjelica.v_cliente to PUBLIC;
create view anjelica.v_cliprv as 
select 'E' as tipo, cli_codi as codigo, cli_nomb as nombre from anjelica.v_cliente 
union all
select 'C' AS tipo,prv_codi as codigo, prv_nomb as nombre from anjelica.v_proveedo;
grant select on anjelica.v_cliprv to public;
-- Puesta linea precio oferta de albaran a not null.
alter table anjelica.v_albavel alter avl_profer set not  null;
-- Añadido campo kilos originales en v_albvenpar
alter table anjelica.v_albvenpar rename avp_tiplot to avp_canori;
alter table anjelica.v_albvenpar ALTER avp_canori type decimal(9,3) USING 0;
update anjelica.ajustedb set aju_regmvt=0;
update anjelica.v_albvenpar set avp_canori = avp_canbru;
alter table anjelica.v_albvenpar ALTER avp_canori   set not null;
--  Añadido campo kilos originales en hisalpave
alter table anjelica.hisalpave rename avp_tiplot to avp_canori;
alter table anjelica.hisalpave ALTER avp_canori type decimal(9,3) USING 0;
update anjelica.ajustedb set aju_regmvt=0;
update anjelica.hisalpave set avp_canori = avp_canbru;
alter table anjelica.hisalpave ALTER avp_canori   set not null;
--- Actualizada vista albaran_detalle
DROP VIEW anjelica.v_albventa_detalle ;
CREATE OR REPLACE VIEW anjelica.v_albventa_detalle AS 
 SELECT c.emp_codi, c.avc_ano, c.avc_serie, c.avc_nume, c.cli_codi, 
    c.avc_clinom, c.avc_fecalb, c.usu_nomb, c.avc_tipfac, c.cli_codfa, 
    c.fvc_ano, c.fvc_nume, c.avc_cerra, c.avc_impres, c.avc_fecemi, c.sbe_codi, 
    c.avc_cobrad, c.avc_obser, c.avc_fecrca, c.avc_basimp, c.avc_kilos, c.avc_unid,
    c.div_codi, c.avc_impalb, c.avc_impcob, c.avc_dtopp, c.avc_dtootr, 
    c.avc_valora, c.fvc_serie, c.avc_depos, l.avl_numlin, l.pro_codi, l.avl_numpal,avl_numcaj,
    l.pro_nomb, l.avl_canti, l.avl_prven, l.avl_prbase, l.tar_preci, l.avl_unid,
    l.avl_canbru, l.avl_fecalt, l.fvl_numlin, l.avl_fecrli, c.alm_codori, 
    c.alm_coddes, p.avp_numlin, p.avp_ejelot, p.avp_emplot, p.avp_serlot, 
    p.avp_numpar, p.avp_numind, p.avp_numuni, p.avp_canti,p.avp_canbru,p.avp_canori
   FROM anjelica.v_albavel l, anjelica.v_albavec c, anjelica.v_albvenpar p
  WHERE c.emp_codi = l.emp_codi AND c.avc_ano = l.avc_ano 
  AND c.avc_serie = l.avc_serie AND c.avc_nume = l.avc_nume AND c.emp_codi = p.emp_codi
  AND c.avc_ano = p.avc_ano AND c.avc_serie = p.avc_serie AND c.avc_nume = p.avc_nume
  AND l.avl_numlin = p.avl_numlin;
grant select on anjelica.v_albventa_detalle to public;
drop view anjelica.v_halbventa_detalle;
create view anjelica.v_halbventa_detalle as 
select c.emp_codi,c.avc_ano,c.avc_serie,c.avc_nume,cli_codi,avc_clinom,avc_fecalb, usu_nomb,avc_tipfac, cli_codfa,
fvc_ano,fvc_nume,c.avc_cerra,avc_impres,avc_fecemi,sbe_codi,avc_cobrad,avc_obser,avc_fecrca,
avc_basimp,avc_kilos,avc_unid,div_codi,avc_impalb,avc_impcob,avc_dtopp,avc_dtootr,avc_valora,fvc_serie,c.his_rowid,
avc_depos,l.avl_numlin,l.pro_codi,avl_numpal,pro_nomb,avl_canti,avl_prven,avl_prbase,tar_preci,avl_unid,
avl_canbru,avl_fecalt,fvl_numlin,avl_fecrli,alm_codori,alm_coddes,
avp_numlin,avp_ejelot,avp_emplot,avp_serlot,avp_numpar,avp_numind,avp_numuni,avp_canti,avp_canbru,avp_canori
from hisalcave as c, hisallive as l, hisalpave as p 
where c.his_rowid=l.his_rowid and l.his_rowid=p.his_rowid
and l.avl_numlin=p.avl_numlin;
grant select on anjelica.v_halbventa_detalle to public;
update anjelica.ajustedb set aju_regmvt=1;
-- Cambiar codigo rgs_partid por par_codi en regalmacen
alter table anjelica.regalmacen rename rgs_partid to par_codi;

-- Cambiada tabla transportistas . Añadida opcion transportista Ventas
alter table v_transport rename to transportista;
alter table transportista add tra_tipo char(1) not null default 'C';
create view v_transport as select * from transportista where tra_tipo='C'; -- Transportista Compras
create view v_tranpvent as select 'T' || tra_codi as tra_codi,tra_nomb from transportista where tra_tipo='V'
UNION
select usu_nomb as tra_codi, usu_nomco as tra_nomb from usuarios where usu_activ='S'; -- Transportista Ventas
grant select on  v_transport to public;
grant select on  v_tranpvent to public;
-- Añadidas lineas de horario reparto en cliente y en hoja de ruta.
alter table anjelica.clientes add cli_horenv varchar(50);
alter table anjelica.clientes add  cli_comenv varchar(80);

alter table anjelica.cliencamb add cli_horenv varchar(50);
alter table anjelica.cliencamb add  cli_comenv varchar(80);

drop view anjelica.v_cliprv;
drop view anjelica.v_cliente;
create or replace view anjelica.v_cliente as select * from anjelica.clientes;
grant select on anjelica.v_cliente to public;
create view anjelica.v_cliprv as 
select 'E' as tipo, cli_codi as codigo, cli_nomb as nombre from anjelica.v_cliente 
union all
select 'C' AS tipo,prv_codi as codigo, prv_nomb as nombre from anjelica.v_proveedo;
grant select on anjelica.v_cliprv to public;

drop view anjelica.v_albruta;
alter table anjelica.albrutalin add alr_horrep varchar(50);
alter table anjelica.albrutalin add alr_comrep varchar(80);

create or replace view v_albruta as select c.*,l.alr_orden,l.avc_id,alr_bultos,alr_palets,
alr_unid,alr_kilos,alr_horrep,alr_comrep,alr_repet,
al.emp_codi,al.avc_ano,al.avc_serie,al.avc_nume,al.cli_codi,al.avc_clinom,al.avc_kilos,
al.avc_unid 
from anjelica.albrutacab as c, anjelica.albrutalin as l,anjelica.v_albavec as al 
where c.alr_nume=l.alr_nume and al.avc_id = l.avc_id;
grant select on v_albruta to public;
-- Añadido congelado a accion de lineas de partes
alter table partelin drop constraint partelin_pal_accion_check;
alter table partelin  add constraint partelin_pal_accion_check check (pal_accion  in ('A','V','R','C','-'));
-
---
-- Cambiar Tabla v_empresa por empresa y crear vista v_empresa
alter table v_empresa rename to empresa;
CREATE or replace VIEW v_empresa as select emp_codi,emp_nomb ,	 -- Nombre de Empresa
 emp_dire,	 -- Direccion Empresa
 emp_pobl,	 -- Poblacion Empresa
 emp_codpo ,	    	 -- Codigo Postal
 emp_telef ,	 -- Telefono
 emp_fax ,	 -- FAX
 emp_nif ,	 -- NIF
 emp_nurgsa , -- Codigo Numero Registro Sanitario
 emp_nomsoc , -- Nombre Social
 pai_codi ,	    	 -- Pais por defecto
 emp_orgcon , -- Organismo de Control.
 emp_cercal , -- Certificacion Calidad
 emp_labcal , -- Etiqueta Calidad
 emp_obsfra ,--Observaciones Factura
 emp_obsalb ,--Observaciones Albaran
 emp_vetnom , --Nombre Veterinario
 emp_vetnum , --Numero Veterinario
 emp_numexp , --Numero Explotacion
 emp_codcom ,         -- Comunidad de Empresa
 emp_codpvi ,    	 -- Provincia de Empresa
 emp_divimp ,         -- Divisa de Importacion
 emp_divexp ,         -- Divisa de Exportación
 emp_desspr , -- Destino Subproductos
 emp_codedi , -- Codigo EDI
 emp_regmer , -- Registro Mercantil
 emp_dirweb , -- Direccion web de la empresa 
 cop_nombre as emp_nomprv
 from empresa left join   prov_espana on emp_codpo / 1000 = cop_codi;
 grant select on v_empresa to public;
-- Añadir campo kilos y unidades a lineas alb. ruta. Añadir estado a cabecera.
drop view v_albruta;
alter table anjelica.albrutacab add alr_cerrad smallint not null default 0;
alter table anjelica.albrutalin add alr_bultos int  ; -- Kilos de Albaran
alter table anjelica.albrutalin add alr_kilos float ; -- Kilos de Albaran
alter table anjelica.albrutalin add alr_palets smallint not null default 1; -- Palets
alter table anjelica.albrutalin add alr_unid int; -- Unidades de Albaran smallint not null default 0;
create or replace view v_albruta as select c.*,l.alr_orden,l.avc_id,alr_bultos,alr_palets,
alr_unid,alr_kilos,alr_repet,
al.emp_codi,al.avc_ano,al.avc_serie,al.avc_nume,al.cli_codi,al.avc_clinom,al.avc_kilos,
al.avc_unid 
from albrutacab as c, albrutalin as lstgr,v_albavec as al 
where c.alr_nume=l.alr_nume and al.avc_id = l.avc_id;
grant select on v_albruta to public;

-- Crear campo ID en factura de ventas.
alter table anjelica.v_facvec add  fvc_id serial not null;

-- Cambiar fecha creacion y fecha modificacion de tabla Stock/Partidas
drop view v_stkpart;
alter table stockpart alter stp_feccre type timestamp;
alter table stockpart alter stp_feccre set not null;
alter table stockpart alter stp_feccre set  default current_timestamp;

 alter table stockpart alter stp_fefici type timestamp;

create or replace view anjelica.v_stkpart as select * from anjelica.stockpart;
grant select on anjelica.v_stkpart to PUBLIC;
-- Añadir columna rut_codi a tabla clientes
drop view v_cliprv;
drop view v_cliente;
alter table clientes rename cli_Valor to rut_codi;
alter table clientes alter rut_codi type  varchar(2);

alter table cliencamb rename cli_Valor to rut_codi;
alter table cliencamb alter rut_codi type  varchar(2);

create view anjelica.v_cliente as select * from anjelica.clientes;
grant select on anjelica.v_cliente to PUBLIC;

create view anjelica.v_cliprv as 
select 'E' as tipo, cli_codi as codigo, cli_nomb as nombre from anjelica.v_cliente 
union all
select 'C' AS tipo,prv_codi as codigo, prv_nomb as nombre from anjelica.v_proveedo;
grant select on anjelica.v_cliprv to public;
---
-- Quitar columna rgs_prebas de regularizaciones almacen
drop view v_inventar;
drop view v_regstock;
alter table regalmacen drop  rgs_prebas;
create view anjelica.v_regstock as select r.*,  tir_nomb, tir_afestk,  tir_tipo 
from anjelica.regalmacen as r,anjelica.motregu as m 
    where r.tir_codi = m.tir_codi 
    and tir_afestk != '*' and rgs_trasp!=0;  
grant select on anjelica.v_regstock to public;
create view anjelica.v_inventar as select r.* 
from anjelica.regalmacen as r,anjelica.motregu as m 
    where r.tir_codi = m.tir_codi 
    and tir_afestk = '=';  
grant select on anjelica.v_inventar to public;
---
-- Incluyendo campo kilos brutos en detalle lineas de albaran
alter table anjelica.v_albvenpar alter avp_canti set not null;
alter table anjelica.v_albvenpar add avp_canbru decimal(9,3) not null default 0;
alter table anjelica.v_albvenpar add avp_canbru decimal(9,3) not null default 0;
alter table anjelica.hisalpave add avp_canbru decimal(9,3) not null default 0;

-- Incluir ID de albaran de venta
alter table v_albavec add avc_id serial;
alter table hisalcave add avc_id int;

-- Incluido almacen en lineas de inventario control
alter table coninvlin add alm_codlin int;
update coninvlin set alm_codlin = (select alm_codi from coninvcab where coninvcab.cci_codi= coninvlin.cci_codi);

alter table coninvlin alter alm_codlin set not null;
drop view anjelica.v_coninvent;
create view anjelica.v_coninvent as
select c.emp_codi,c.cci_codi,c.usu_nomb,cci_feccon, cam_codi,alm_codi,lci_nume,prp_ano, prp_empcod, prp_seri, prp_part, pro_codi, pro_nomb,
prp_indi,lci_peso,lci_kgsord,lci_numind,lci_regaut,lci_coment,lci_numpal,alm_codlin from coninvcab as c, coninvlin as l where
c.emp_codi=c.emp_codi
and c.cci_codi=l.cci_codi;
grant select on  anjelica.v_coninvent to public;

-- Incluido nombre abreviado
alter table paises add pai_nomcor varchar(5);
update paises set pai_nomcor = substring(pai_nomb,1, 5);
alter table paises alter pai_nomcor set not null;
drop view anjelica.v_paises;
create view anjelica.v_paises as select * from paises ;
grant select on  anjelica.v_paises to public;
-- Update para poner el numero de palet y caja a stock-partidas
update stockpart set stp_numpal=(select avl_numpal from v_albventa_detalle as a
	where alm_coddes=3 and avc_serie='X' and stockpart.pro_codi=a.pro_codi
and stockpart.eje_nume=avp_ejelot
and stockpart.pro_serie=avp_serlot
and stockpart.pro_nupar=avp_numpar
and stockpart.pro_numind=avp_numind), 
stp_numcaj=(select avl_numcaj from v_albventa_detalle as a
where alm_coddes=3 and avc_serie='X' and stockpart.pro_codi=a.pro_codi
and stockpart.eje_nume=avp_ejelot
and stockpart.pro_serie=avp_serlot
and stockpart.pro_nupar=avp_numpar
and stockpart.pro_numind=avp_numind)
where alm_codi=3 and stp_kilact > 0 and exists (select * from v_albventa_detalle as a
where alm_coddes=3 and avc_serie='X' and stockpart.pro_codi=a.pro_codi
and stockpart.eje_nume=avp_ejelot
and stockpart.pro_serie=avp_serlot
and stockpart.pro_nupar=avp_numpar
and stockpart.pro_numind=avp_numind);
--- Añadido campo palet y caja a stock-partidas
alter table anjelica.stockpart  add stp_numpal smallint;
alter table anjelica.stockpart  add stp_numcaj smallint;
create or replace view anjelica.v_stkpart as select * from anjelica.stockpart;
---
alter table anjelica.almacen rename tipo to alm_tipo;
alter table anjelica.almacen alter alm_tipo  set default 'I';
alter table anjelica.almacen alter alm_tipo  set NOT NULL;

alter table anjelica.v_articulo add pro_indtco int not null default -1;

-- Diferencias con version 1.0 de Anjelica

update v_regstock set acc_ano=eje_nume,acc_nume=pro_nupar,acc_Serie=pro_serie where rgs_recprv <> 0;

--  Añadido serie Y (para albaranes internos)
alter table v_numerac rename num_indiv to num_serieY;
alter table v_numerac rename num_prec1 to num_secomY;
-- Cambio en la tabla de Usuarios
alter table usuarios add sbe_codi smallint;
update usuarios set sbe_codi=1;
alter table usuarios alter sbe_codi set  not null;
-- Incluido el campo cli_intern en tabla clientes.
alter table clientes add cli_intern smallint;
update clientes set cli_intern=0;
alter table clientes alter cli_intern set not null;
------

alter table anjelica.clientes alter cli_email1  type char(60);
alter table anjelica.clientes alter cli_email2  type char(60);
-- Hacer un pg_dump de la table cliencamb
-- pg_dump -i -F p -a -v -f "cliencamb.sql" -t cliencamb anjelica
-- Volver a crear la tabla y restaurar datos.
--
-- Modificacion Proveedores
alter table v_proveedo add prv_intern smallint;
update v_proveedo set prv_intern=0;
alter table v_proveedo alter prv_intern set not null;
-- MOdificacion Cabecera Pedidos Ventas
alter table pedicoc add sbe_codi smallint;
update pedicoc set sbe_codi=1;
alter table pedicoc alter sbe_codi set not null;
-- Modificacion tabla almacenes
alter table almacen add emp_codi smallint;
update almacen set emp_codi=1;
alter table almacen alter emp_codi set not null;

alter table almacen add sbe_codi smallint;
update almacen set sbe_codi=1;
alter table almacen alter sbe_codi set not null;

alter table almacen drop CONSTRAINT ix_almacen;
alter table almacen add CONSTRAINT ix_almacen unique (emp_codi,alm_codi);
-- Actualizando Cabecera Alb. Compras
alter table v_albacoc add avc_ano smallint;
alter table v_albacoc add avc_nume smallint;
-- Actualizando Lineas Alb. Compras
alter table v_albacol add acl_prstk decimal(9,3);
update v_albacol set acl_prstk = 0;
alter table v_albacol alter acl_prstk set not null;
-- Meto la nueva etiqueta disponible
-- insert into etiquetas values (1,7,'STDANDARD SIN FEC.CAD','ibr3.jpg','etiqueta7','N');
--- Modificar campo plantilla to pro_oblfsa (Obligatorio Fecha Sacrificio)
alter table v_articulo rename plantilla to pro_oblfsa;
alter table v_articulo alter pro_oblfsa type smallint;
update v_articulo set pro_oblfsa=0;
alter table v_articulo alter pro_oblfsa set not null;

-- Insertar campos numeros en numeracion
alter table v_numerac add num_factub int not null default 0;
alter table v_numerac add num_factuc int not null default 0;
alter table v_numerac add num_factud int not null default 0;
alter table v_numerac add num_sefral smallint not null default 0;

-- Incluir en Lineas de Inventario el campo para activar si es reg. Automatico
alter table coninvlin add lci_regaut smallint not null default 0;
-- Incluir campo comentario
alter table coninvlin add lci_coment varchar(25);
-- Incluir la serie en la factura de Ventas
alter table v_facvec add fvc_serie char(1) not null default 1;
alter table v_facvec drop constraint ix_facvec;
alter table v_facvec add constraint ix_facvec primary  key (fvc_ano,emp_codi,fvc_serie,fvc_nume);
alter table v_facvel add fvc_serie char(1) not null default 1;
alter table v_facvel drop constraint ix_facvel;
alter table v_facvel add constraint ix_facvel  primary key (eje_nume,emp_codi,fvc_serie,fvc_nume,fvl_numlin);

-- Incluir serie de factura en cobros.
alter table v_cobros add fvc_serie char(1);
update v_cobros set fvc_serie='1' where fac_nume!=0;
-- Incluir serie de factura en cabecera albaranes de compras
alter table v_albavec add fvc_serie char(1);
update v_albavec set fvc_serie='1' where fvc_nume!=0;
-- Incluir serie factura en Recibos
alter table v_recibo add fvc_serie char(1) not null default 1;
alter table v_recibo drop constraint v_recibo_pkey;
alter table v_recibo add constraint ix_recibo  primary key (eje_nume,emp_codi,fvc_serie,fvc_nume,rec_nume);
-- Facturas en cobro por las rutas.
alter table factruta  add fvc_serie char(1) default 1;
update factruta set fvc_serie='1' ;
alter table factruta alter fvc_serie set not null;

-- Actualizar MENUS
update menus set mnu_prog='gnu.chu.anjelica.riesgos.ClRiesClien' where mnu_prog like '%clriescli%';
update menus set mnu_prog='gnu.chu.anjelica.facturacion.PadFactur modCons=false' where mnu_prog like '%PadFactu%';

-- Pone a la tabla etiquetas el campo eti_client que indica si esa etiqueta
-- es especifica de un cliente
alter table  etiquetas add eti_client smallint;
update etiquetas set eti_client=0;
alter table  etiquetas alter eti_client set not null;

alter table  clientes add eti_codi smallint;
update clientes set eti_codi=0;
alter table  clientes alter eti_codi set not null;
-- La tabla cliencamb se cambia volviendola a crear de nuevo.

alter table  albvefax add avf_tipdoc char(1) default 'A';      -- Tipo Documento (A:Albaran, F:Factura)
update albvefax set avf_tipdoc='A';
alter table  albvefax alter  avf_tipdoc set not null;

-- Le pongo constraints a la tabla clientes
alter table v_albavec add constraint avc_procl foreign key (cli_codi) references clientes(cli_codi);
alter table v_albavel add constraint avl_profk foreign key (pro_codi) references v_articulo(pro_codi);

-- Pongo campo cli_codi en configuracion y le pongo valor por defecto 9999.
alter table  configuracion add cli_codi int;
update configuracion set cli_codi=9999;
alter table  configuracion alter  cli_codi set not null;

-- Cambio campo pro_numeti para incluir numeros de crotal por individuo
alter table v_articulo  rename pro_numeti to pro_numcro;
update v_articulo set pro_numcro =0;
alter table  v_articulo alter pro_numcro set not null;
-- Incluyo campo con el numero de albaran anteriormente cobrado
alter table v_cobros add cob_albar char(7);
--  Modificar campo en tabla clientes que dice si los precios son a revisar por representante.
alter table clientes alter cli_precfi type int;
alter table clientes alter cli_precfi set default 0;
update clientes set cli_precfi=0;
alter table clientes alter cli_precfi set not null;
-- Modificar  cabecera albaran de ventas para incluir campo revisar precios
alter table v_albavec rename avc_tainpr to avc_revpre;
alter table v_albavec alter avc_revpre set default 0;
update v_albavec set avc_revpre=0;
alter table v_albavec alter avc_revpre set not null;
-- Pongo el Precio base segun precio venta 
update v_albavel set avl_prbase = avl_prven - (avl_prven * (select (c.avc_dtopp+c.avc_dtocom)/100  from v_albavec as c
 where c.emp_codi = 1 
and c.avc_ano=2010   and c.avc_nume=v_albavel.avc_nume and c.avc_serie=v_albavel.avc_serie) )
where avl_prven != avl_prbase 
and avc_ano=2010 
-- Renombrar campo en Lineas de albaranes para incluir comentario
alter table v_albavel rename aux_1 to avl_coment;

alter table v_articulo add constraint fam_profk
   foreign key (emp_codi,fam_codi) references v_famipro(fpr_codi);

alter table v_agupro alter emp_codi set not null;
alter table v_agupro drop constraint ix_agrupo;
alter table v_agupro add constraint ix_agrupo primary key(emp_codi,agr_codi);
-- Incluir precio introducido por el usuario en despiece final.
alter table v_despfin add def_preusu float not null default 0;
--
alter table v_desporig add deo_preusu float not null default 0;
-- Poner dto de lineas de facturas de compras a not null default 0
alter table v_falico alter fcl_dto set not null;
alter table v_falico alter fcl_dto set default '0';

alter table usuarios add usu_pass varchar(50);       -- Contraseña Usuario (en SHA-1)

alter table v_articulo add pro_activ int default -1 not null;

DROP INDEX IX_TIPODESP;
ALTER TABLE tipodesp add constraint IX_TIPODESP primary key(tid_codi);
drop index ix_tipdesent;
ALTER TABLE tipdesent add constraint ix_tipdesent primary key(tid_codi,tde_nuli);

-- Incluir campo costo en los productos de salida de la tabla tipos despieces
alter table tipdessal  add tds_costo float;
update tipdessal set tds_costo=0;
alter table tipdessal alter tds_costo set not null;

alter table clientes add zon_codi char(2);
alter table clientes add rep_codi char(2);
alter table clientes add cli_feulve date;
alter table clientes add cli_feulco date;
alter table clientes add cli_estcon char(1) default 'N';

update clientes set cli_feulve  = (SELECT MAX(avc_fecalb) as avc_fecalb FROM V_ALBAVEC WHERE V_ALBAVEC.CLI_CODI = clientes.cli_codi);
-- Manualmente hay que hacer drop de la tabla cliencamb y volverla a crear con
-- campos zon_codi y rep_codi

-- Incluir Acumulados de Kilos e Importe en cabecera de albaran
alter table v_albavec rename avc_sumto2 to avc_basimp;
update v_albavec set avc_basimp = (select sum(((avl_prven-avl_dtolin) * avl_canti) - (((avl_prven-avl_dtolin) * avl_canti) * (avc_dtopp+avc_dtocom/100)))
from v_albavel as l where
   v_albavec.emp_codi = l.emp_codi
              and v_albavec.avc_ano = l.avc_ano
              and v_albavec.avc_serie = l.avc_serie
              and v_albavec.avc_nume = l.avc_nume);
update  v_albavec set avc_basimp=0 where avc_basimp is null;
alter table v_albavec alter avc_basimp set not  null;
alter table v_albavec alter avc_basimp set not null;

alter table v_albavec rename avc_impuv2 to avc_kilos;       -- Suma de Kilos.
update v_albavec set avc_kilos = (select sum( avl_canti)
from v_albavel as l where
   v_albavec.emp_codi = l.emp_codi
              and v_albavec.avc_ano = l.avc_ano
              and v_albavec.avc_serie = l.avc_serie
              and v_albavec.avc_nume = l.avc_nume);
update  v_albavec set avc_kilos=0 where avc_kilos is null;
alter table v_albavec alter avc_kilos set not null;

alter table clientes add cli_email1 char(40);
alter table clientes add cli_email2 char(40);

alter table grupdesp add grd_fecval timestamp;
alter table grupdesp add grd_usuval varchar(20);

alter table v_albavel add avl_fecalt date default current_date;
update v_albavel set avl_fecalt = (select avc_fecalb
from v_albavec as c where
   v_albavel.emp_codi = c.emp_codi
              and v_albavel.avc_ano = c.avc_ano
              and v_albavel.avc_serie = c.avc_serie
              and v_albavel.avc_nume = c.avc_nume) where avl_fecalt is null;

alter table grupdesp alter grd_block set default 'N';
update grupdesp set grd_block ='N' where grd_block='';
-- Quito unico index en salidas de despieces y pongo indice normal.
alter table v_despfin drop CONSTRAINT ix_despfin;
create index  ix_despfin  on v_despfin (eje_nume,emp_codi,deo_codi,def_orden);
-- Añado campos para registrar fecha de origen despiece, y final despiece
alter table v_desporig add deo_fecalt date default current_date;
update v_desporig set deo_fecalt = deo_fecha where deo_fecalt is null;
alter table v_despfin add def_fecalt date default current_date;
update v_despfin set def_fecalt = (select max(deo_fecalt)
from v_desporig as c where
   v_despfin.emp_codi = c.emp_codi
              and v_despfin.eje_nume = c.eje_nume
              and v_despfin.def_numdes = c.deo_numdes) where def_fecalt is null;
-- Incluyo fecha de produccion.
alter table grupdesp add grd_fecpro date;
-- Incluyo dias de caducidad cogelado en ficha de produto
alter table v_articulo add pro_cadcong int default 23 not null;

-- Incluyo Camara como un campo aparte.
alter table v_articulo add cam_codi char(2);
update v_articulo set cam_codi=pro_disc3;
alter table v_articulo alter cam_codi set not null;
-- Incluyo campo que indica si el producto es envasado al vacio
alter table v_articulo add pro_envvac int default 0 not null;

update v_articulo set cam_codi=pro_disc3;
alter table v_articulo alter cam_codi set not null;

-- Creo la tabla que relaciona familias con productos
create table grufampro
(
agr_codi int not null, -- Grupo
fpr_codi int not null -- Familia
);
--
-- Modificaciones para depositos en almacenes
--
alter table v_albavec add avc_depos char(1) default 'N' NOT NULL;
-- Añadir tablas albvenseri,albvenserl,albvenserc

-- Incluir Numero de Alb. de Proveedor en cabecera alb.
alter table v_albacoc add apc_nume int;
-- Incluir clasificacion en producto.
-- alter table v_albcompar add acp_clasif char(1) default 'X' NOT NULL;
alter table v_albcompar rename guiasanitariafecha to acp_fecpro;
alter table v_albcompar rename guiasanitariacomunidad to dre_nume;
-- Incluyendo tipo para subempresa.
ALTER TABLE subempresa add sbe_tipo char(1) not null default 'C';

-- Modifico tabla de articulos para incluir fecha alta/modificacion y usuario
alter table v_articulo add pro_fecalt date;
update v_articulo set pro_fecalt='20000101';
alter table v_articulo alter pro_fecalt set not null;
alter table v_articulo add pro_feulmo date;
alter table v_articulo add usu_nomb varchar(15);
update v_articulo set usu_nomb='anjelica';
alter table v_articulo alter usu_nomb set not null;

-- Modifico stock partidas para incluir el campo stk_block
alter table v_stkpart rename stk_nlipar to stk_block;
update v_stkpart set stk_block=0;
alter table v_stkpart alter stk_block set not null;
alter table v_stkpart alter stk_block set default 0;

-- Modifico tabla Tipos Despiece
alter table tipodesp add tid_activ int;
alter table tipodesp add tid_fecalt date;
alter table tipodesp add tid_feulmo date; -- Fecha Ultima Modificación
alter table tipodesp add usu_nomb varchar(15);
update tipodesp set tid_activ=-1;
alter table tipodesp alter tid_activ set not null;
update tipodesp set tid_fecalt='20000101';
alter table tipodesp alter tid_fecalt set not null;
-- Hago mas largo el campo de la contraseña
alter table usuarios alter usu_pass type varchar(80)

-- Nuevos indices para despieces, quitando la empresa.
drop index ix_grupdesp;
create index ix_grupdesp on grupdesp(eje_nume,grd_nume);
drop index ix_despori2;
create index ix_despori2 on v_desporig(eje_nume,deo_numdes);
drop index  ix_despfin;
create index  ix_despfin  on v_despfin (eje_nume,deo_codi,def_orden);

--Nuevo programa para mantenimiento tipos despieces.
update  menus set mnu_prog='gnu.chu.anjelica.despiece.MantTipDesp' where mnu_prog like '%pdtipdes%';
-- Añadido campo para indicar si es tipo de despiece de agrupacion
alter table tipodesp add tid_agrup int not null default 0; 
-- Modificando tabla de grupos de despieces
--
alter table grupdesp add grd_fecha date not null default current_date;

-- Poner nuevo programa Listado Diferencias inventarios en el Menu
update menus set mnu_prog='gnu.chu.anjelica.inventario.ClDifInv' where mnu_prog like '%lidiexct%'; 
-- Borrar todas las empresas, excepto la 1
DELETE FROM V_ALBVENPAR WHERE EMP_CODI!=1;
DELETE FROM V_ALBAVEL WHERE EMP_CODI!=1;
DELETE FROM V_ALBAVEC WHERE EMP_CODI!=1;
DELETE FROM v_facvec WHERE EMP_CODI!=1;
DELETE FROM v_facvel WHERE EMP_CODI!=1;
delete from v_albacoc where emp_Codi!=1;
delete from v_albacol where emp_Codi!=1;
delete from v_empresa where emp_codi!=1;
delete from v_numerac where emp_codi!=1;
delete from configuracion where emp_codi!=1;

-- Mejorando velocidad de calculo de inventarios
 create index ix_coninvli3 on coninvlin (cci_codi,pro_Codi);
alter table coninvcab drop CONSTRAINT ix_coninvcab;
alter table coninvcab add CONSTRAINT ix_coninvcab unique (cci_codi,emp_codi);
alter table desporig alter deo_numuni set default 0;

-- Incluyendo current_timestamp en lineas despieces
alter table desorilin add deo_tiempo timestamp default current_timestamp;
alter table v_despfin rename def_fecalt to def_tiempo;
alter table v_despfin alter def_tiempo type timestamp;

-- Añadido campo para indicar si permite usar productos equivalentes
alter table tipodesp add tid_usoequ smallint not null default -1;
-- Alterando tabla de paises
alter table v_paises rename to paises;
alter table paises drop textoaecoc;
alter table paises add pai_activ smallint not null default -1;
alter table paises rename estructuracrotal to pai_estcro;
alter table paises alter pai_codi set not null;
alter table paises alter pai_nomb set not null;
alter table paises alter pai_inic set not null;
create view v_paises as select * from paises;
update paises set pai_activ=0;
UPDATE paises set pai_activ=-1 where pai_codi=64 ;
UPDATE paises set pai_activ=-1 where pai_codi=14 ;
UPDATE paises set pai_activ=-1 where pai_codi=1022 ;
UPDATE paises set pai_activ=-1 where pai_codi=55 ;
UPDATE paises set pai_activ=-1 where pai_codi=800 ;
UPDATE paises set pai_activ=-1 where pai_codi=8 ;
UPDATE paises set pai_activ=-1 where pai_codi=17 ;
UPDATE paises set pai_activ=-1 where pai_codi=1 ;
UPDATE paises set pai_activ=-1 where pai_codi=10 ;
UPDATE paises set pai_activ=-1 where pai_codi=11 ;
UPDATE paises set pai_activ=-1 where pai_codi=4 ;
UPDATE paises set pai_activ=-1 where pai_codi=5 ;
UPDATE paises set pai_activ=-1 where pai_codi=1008 ;
UPDATE paises set pai_activ=-1 where pai_codi=44 ;
UPDATE paises set pai_activ=-1 where pai_codi=60 ;
UPDATE paises set pai_activ=-1 where pai_codi=63 ;
UPDATE paises set pai_activ=-1 where pai_codi=7 ;
UPDATE paises set pai_activ=-1 where pai_codi=0 ;
UPDATE paises set pai_activ=-1 where pai_codi=91 ;
UPDATE paises set pai_activ=-1 where pai_codi=6 ;
UPDATE paises set pai_activ=-1 where pai_codi=400 ;
UPDATE paises set pai_activ=-1 where pai_codi=53 ;
UPDATE paises set pai_activ=-1 where pai_codi=1000 ;
UPDATE paises set pai_activ=-1 where pai_codi=2 ;
UPDATE paises set pai_activ=-1 where pai_codi=99995 ;
UPDATE paises set pai_activ=-1 where pai_codi=406 ;
--
-- Poniendo como timestamp la fecha carga de linea de albaranes
alter table v_albavel alter avl_fecalt type timestamp;
alter table v_albavel alter avl_Fecalt set default current_timestamp;
-- Poniendo como timestamp la fecha de regulirizaciones
alter table v_regstock alter rgs_fecha type timestamp;
alter table v_regstock drop rgs_hora;
-- Tabla linea de albaranes compras.
-- Cambiar nombre de columna que indica si es de portes pagados.
alter table v_albacol rename acl_cance to acl_porpag;
-- Añadir campo descuento Pronto Pago.
alter table v_albacol add acl_dtopp float not null default 0;
-- Tabla articulos, añadir campo para costo incrementado
alter table v_articulo add pro_cosinc float default 0 not null;
alter table v_articulo add pro_mancos float default 0 not null;
alter table anjelica.v_articulo alter pro_codart set not null;

-- Añadir Campo  Palet a Control Inventario
alter table coninvlin add lci_numpal char(5);
--
-- Incluir fecha Inicio y Final a tabla tipos de iva
alter table tiposiva add tii_fecini date;
alter table tiposiva add tii_fecfin date;
update tiposiva set tii_Fecini='20000101', tii_fecfin='20120831';
alter table tiposiva alter tii_fecini set not null;
alter table tiposiva alter tii_fecfin set not null;

-- Poner tipo decimal 8,2 a campos de facturas de venta
alter table v_facvec alter  column fvc_sumtot type decimal(8,2) ;
alter table v_facvec alter  column fvc_impiva type decimal(8,2) ;
alter table anjelica.almacen rename tipo to alm_tipo;
alter table anjelica.almacen alter alm_tipo  set default 'I';
alter table anjelica.almacen alter alm_tipo  set NOT NULL;

alter table v_facvec alter  column fvc_imprec type decimal(8,2) ;
-- Incluir campos de  ventas x articulos vendibles en historicos
alter table histventas add hve_kiveav float;
alter table histventas add hve_imveav float;
--
--
alter table tiposiva drop constraint ix_tiposiva;
alter table tiposiva add constraint ix_tiposiva primary key(tii_codi,tii_fecini,tii_fecfin);
-- Constraints para que no se puedan meter lotes con valor cero en despieces
update v_despfin set pro_numind = 9999 where pro_numind=0;
update v_despfin set pro_lote = 1 where pro_lote=0;
alter table desorilin ALTER pro_lote set not null;
alter table v_Despfin ALTER pro_lote set not null;
alter table v_despfin add constraint lote_positivo CHECK (pro_lote > 0);
alter table v_despfin add constraint indi_positivo CHECK (pro_numind > 0);
alter table desorilin add constraint lote_positivo CHECK (pro_lote > 0);

-- Puesto campo para incluir numero de palet.
alter table anjelica.v_albavel rename turno to avl_numpal;
alter table anjelica.v_albavel alter avl_numpal set   default 0;
alter table anjelica.v_albavel alter avl_numpal set  not null;
alter table anjelica.hisallive add avl_numpal int not null default 0;
alter table hisallive rename column turno to avl_numpal;
alter table hisalbavel rename column turno to avl_numpal;

-- Puesto campo para incluir numero de Caja.
alter table anjelica.v_albavel rename avl_trapa to avl_numcaj;
alter table anjelica.v_albavel alter avl_numcaj set   default 0;
alter table anjelica.v_albavel alter avl_numcaj set  not null;
alter table anjelica.hisallive add avl_numcaj smallint not null default 0;

alter table hisallive rename column avl_Trapa to avl_numcaj;
alter table hisalbavel rename column avl_Trapa to avl_numcaj;
--
--- Triggers
drop TRIGGER if_desp_updf on  v_Despfin cascade;
DROP FUNCTION check_desp();
create  function check_desp() returns  opaque AS  '
DECLARE
GRDBLOCK VARCHAR;
BEGIN
select  INTO GRDBLOCK grd_block from grupdesp where emp_codi = NEW.emp_codi
and eje_nume=NEW.eje_nume
and grd_nume=NEW.def_numdes
AND GRD_BLOCK=''S'';
IF  FOUND THEN
   RAISE EXCEPTION ''GRUPO ESTA BLOQUEAD0'';
END IF;
return NEW;
end;
'language 'plpgsql';
 CREATE TRIGGER if_desp_updf
   BEFORE INSERT OR UPDATE OR DELETE ON v_despfin
      FOR EACH ROW
          EXECUTE PROCEDURE check_desp();

--
-- Para comprobar si hay entregas huerfanas o mal puestas
--
select p.avc_nume,e.* from v_albvenserv as e, v_albvenpar as p where e.avs_ejelot=p.avp_ejelot and e.avs_numpar=p.avp_numpar and e.avs_numind=p.avp_numind
and e.avs_serlot=p.avp_serlot and e.pro_codi = p.pro_codi and not exists (select * from v_albvenpar as p1 where
p1.avc_nume=e.avc_nume and
p1.avc_ano=e.avc_ano and p1.emp_codi=e.emp_codi and p1.avc_serie=e.avc_serie and
e.avs_ejelot=p1.avp_ejelot and e.avs_numpar=p1.avp_numpar and e.avs_numind=p1.avp_numind
and e.avs_serlot=p1.avp_serlot and e.pro_codi = p1.pro_codi) order by avs_nume

-- 
--  Comprobar lineas de final de despiece borradas.
-- 
select * from v_stkpart as s where eje_nume=2011 and pro_serie='T' and pro_nupar=33185
and not exists (select * from v_despfin as f where eje_nume=2011 and pro_lote= s.pro_nupar and s.pro_numind= f.pro_numind and f.pro_codi=s.pro_codi); 

-- Poner como inactivos articulos sin mvtos ni stock.
update v_articulo set pro_activ=0 
where pro_activ!=0
and pro_tiplot='V' 
and NOT EXISTS (select PRO_CODI from mvtosalm  as m where m.pro_codi =v_articulo.pro_codi and m.mvt_fecdoc<'20150101')
and not exists (select pro_codi from v_regstock as m where m.pro_codi =v_articulo.pro_codi and m.rgs_fecha <'20150101' )

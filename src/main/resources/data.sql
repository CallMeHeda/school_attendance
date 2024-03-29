MERGE into TCOURS(CODE,NB_PERIODES,NOM) values 
('IGEB',60,'Gestion de base de donn�es'),
('IPID',60,'Projet de D�veloppement'),
('IVTE',40,'Veille technologique'),
('ISO2',60,'Structure des ordinateurs'),
('XTAB',80,'Base de donn�es et Tableur');

delete from TSECTION;
insert  into TSECTION(FKCOURS,SECTION) values ('XTAB','Comptabilit�');
insert  into TSECTION(FKCOURS,SECTION) values ('XTAB','Secr�tariat');
insert  into TSECTION(FKCOURS,SECTION) values('IPID','Informatique');
insert  into TSECTION(FKCOURS,SECTION) values('IGEB','Informatique');
insert  into TSECTION(FKCOURS,SECTION) values('ISO2','Informatique');
insert  into TSECTION(FKCOURS,SECTION) values('IVTE','Informatique');

 MERGE INTO TUSER VALUES
('admin','$2a$10$BR98DmjHecYVdqtNz3UGcuyFop/ed0KfxOEAp1bdVXh8eF2iAfOz.','0' ),
('vo', '$2a$10$WvAj8iSJyIxDQWHysC4o6usLkzKW5bi9GThqYH2X/w/vVAm18nWcq','1'),
('dh','$2a$10$VVf8g1RgF616qNWQJvMIyeGwrOqKqXlSRg1iWIEEkBKsBvFBv38ka','1'),
('et1','$2a$10$N8pzEKs1350SPT1vpomUo.zMEV1IR/LjXgOrBJ.QaeddZtKLVxR4q',2),
('et2','$2a$10$.Tlzs3a.2PEGK1gckhgaJuz4SWW2J1lWBf.4E3rOleziGDpExtp1W',2),
('et3','$2a$10$eeilUxVqPB8dXMsnzxiIp.OlOyD1isPtICWaRRL5VFofqcWcSURk2',2),
('et4','$2a$10$35yXfuSOyZkLLXFKXQbDLuzvdepJA2cFwxqu.VOsDuuAgf5ujVOuu',2),
('et5','$2a$10$TXAbouJ1PcbhQe7RkKLnOOea60ZxaPHJZvm2VOXR.WyjrPpzihe7K',2),
('et6','$2a$10$fi8m93.jn83zrD8TicH5vuhKZsJUXnnP.TYbR57A8NWHmE3MqhHUS',2),
('secretaire','$2a$10$GPKb0dvOCCG0bTi2dEdGI.3jGD0/q1w3IrUrLr2aamXGaH..gkofq',3);

MERGE INTO TPROFESSEUR(ID,NOM,PRENOM,EMAIL,FKUSER) VALUES
(1,'Van Oudenhove','Didier','vo@isfce.be','vo'),
(2,'De Henau','Marie Ange','dh@isfce.be','dh');

MERGE into TModule(CODE, DATE_DEBUT, DATE_FIN, MOMENT, FKCOURS,FKPROF) VALUES
 ('IPID-1-A', '2020-09-16','2021-01-27',2,'IPID',1),
 ('IVTE-1-A', '2021-02-01','2021-04-19',2,'IVTE',1),
 ('ISO2-1-A', '2020-09-17','2021-01-28',1,'ISO2',1),
 ('ISO2-1-B', '2020-09-17','2021-01-28',2,'ISO2',1),
 ('IGEB-1-A', '2020-03-06','2020-06-03',2,'IGEB',1),
 ('IGEB-2-A', '2020-03-06','2020-06-03',2,'IGEB',1);
 
MERGE INTO TETUDIANT(ID,NOM,PRENOM,EMAIL,TEL,FKUSER) VALUES
(1,'Pitt','Brad','et1@isfce.be','02/647.10.01','et1'),
(2,'Jolie','Angelina','et2@isfce.be','02/647.10.02','et2'),
(3,'Obama','Michelle','et3@isfce.be','02/647.10.03','et3'),
(4,'Obama','Barack','et4@isfce.be','02/647.10.04','et4'),
(5,'Knowles','Beyonc�','et5@isfce.be','02/647.10.03','et5'),
(6,'Suarez','Blanca','et6@isfce.be','02/647.10.04','et6');

MERGE INTO TINSCRIPTION(FKMODULE, FKETUDIANT) VALUES
('IPID-1-A',1),
('IPID-1-A',2),
('IPID-1-A',3),
('IVTE-1-A',5),
('IVTE-1-A',2),
('IVTE-1-A',4),
('ISO2-1-A',1),
('ISO2-1-A',3),
('ISO2-1-A',5),
('ISO2-1-B',6),
('ISO2-1-B',2),
('ISO2-1-B',4);
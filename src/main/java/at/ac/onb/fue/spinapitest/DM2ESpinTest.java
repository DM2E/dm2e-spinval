/**
 * Perform SPIN (spinrdf.org) constraint checks on triples imported from file
 * 
 * Input:
 * OWL file with SPIN constraints
 * Instance File with triples to be checked
 * 
 * Output:
 * List of Contraint Violations
 *
 * @author Doron Goldfarb (Austrian National Library)
 *
 */

package at.ac.onb.fue.spinapitest;
import java.util.List;

import org.topbraid.spin.constraints.ConstraintViolation;
import org.topbraid.spin.constraints.SPINConstraints;
import org.topbraid.spin.inference.SPINInferences;
import org.topbraid.spin.system.SPINLabels;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.constraints.SimplePropertyPath;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DM2ESpinTest 
{
    public static void main( String[] args )
    {
        // Initialize system functions and templates
        SPINModuleRegistry.get().init();

        // Load main file
        Model baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);

        try {
            InputStream owlStream = new FileInputStream("DM2Ev1.0_SPIN.owl");
            baseModel.read(owlStream, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DM2ESpinTest.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
       

        Model dm2eInstData  = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        
        try {
            InputStream instStream = new FileInputStream("ONB_Codices_Testdata.xml");
            dm2eInstData.read(instStream, null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DM2ESpinTest.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }        
        
        
        /*        try {
            OutputStream out = new FileOutputStream("testoutput.rdf");
            dm2eInstData.write(out);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DM2ESpinTest.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        
        // Create OntModel with imports
        OntModel ontModel = JenaUtil.createOntologyModel(OntModelSpec.OWL_MEM,baseModel);
     
        // ADD THE DM2E INST TRIPLES TO the ontmodel
        ontModel.addSubModel(dm2eInstData);

        // Register locally defined functions
        SPINModuleRegistry.get().registerAll(ontModel, null);

        // Check all constraints
        List<ConstraintViolation> cvs = SPINConstraints.check(ontModel, null);
        System.out.println("Constraint violations:");
        for(ConstraintViolation cv : cvs) {
                System.out.println(cv.getRoot() + " " + cv.getMessage());
        }
    }
}

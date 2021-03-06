package org.protege.owl.server.command;

import static org.protege.owl.server.command.P4OWLServerOptions.NEEDS_HELP_OPTION;

import java.io.File;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.protege.owl.server.api.ChangeMetaData;
import org.protege.owl.server.api.client.Client;
import org.protege.owl.server.api.client.VersionedOntologyDocument;
import org.protege.owl.server.util.ClientRegistry;
import org.protege.owl.server.util.ClientUtilities;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class Commit extends ServerCommand {
    private Options options = new Options();
    {
        options.addOption(NEEDS_HELP_OPTION);
    }
    private File ontologyFile;

    @Override
    public boolean parse(String[] args) throws ParseException {
        ontologyFile = parseSingleExistingFile(args, options);
        return ontologyFile != null && ontologyFile.isFile();
    }

    @Override
    public void execute() throws Exception {
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntology(IRI.create(ontologyFile));
        ClientRegistry registry = getClientRegistry();
        if (registry.hasSuitableMetaData(ontology)) {
            Client client = registry.connectToServer(ontology);
            VersionedOntologyDocument vont = registry.getVersionedOntologyDocument(ontology);
            ChangeMetaData metaData = getCommitComment();
            ClientUtilities.commit(client, metaData, vont);
            System.out.println("Changes committed.");
        }
        else {
            System.out.println("Could not connect to appropriate server - no known server metadata found.");
        }
    }

    @Override
    public void usage() {
        usage("Commit <options> ontology-file", "", options);
    }

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        new Commit().run(args);
    }

}

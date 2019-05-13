package com.wikiparser.api.clients

import api.clients.gephiJsonExporter.JSONExporterBuilder
import org.gephi.*

import org.openide.util.Lookup
import org.gephi.graph.api.GraphModel
import org.gephi.graph.api.GraphController
import org.gephi.project.api.Project
import org.gephi.project.api.ProjectController
import org.gephi.project.api.Workspace

import java.net.URL

import java.io.IOException
import org.gephi.io.exporter.api.ExportController
import org.gephi.layout.plugin.AutoLayout
import org.gephi.layout.plugin.forceAtlas.ForceAtlasLayout
import org.gephi.layout.plugin.force.StepDisplacement
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout
import java.util.concurrent.TimeUnit
import org.gephi.graph.api.DirectedGraph
import org.gephi.io.exporter.plugin.ExporterCSV
import org.gephi.io.exporter.spi.CharacterExporter
import org.gephi.io.exporter.spi.Exporter
import org.gephi.io.processor.plugin.DefaultProcessor
import org.gephi.io.importer.api.ImportController
import org.openide.loaders.DataLoader.getLoader
import org.gephi.io.generator.plugin.RandomGraph
import org.gephi.io.importer.api.Container
import org.gephi.io.importer.api.EdgeDirectionDefault
import java.io.File
import java.io.StringWriter


object GephiClient {

    val pc = Lookup.getDefault().lookup(ProjectController::class.java)
    val workspace : Workspace
    val graphModel : GraphModel


    init {
        pc.newProject()
        workspace = pc.currentWorkspace
        graphModel = Lookup.getDefault().lookup(GraphController::class.java).graphModel
    }

    // Calculate coordinates of nodes and convert resulting graph to json string which can be read by client
    fun processGraphToSigmaJsonString(name : String, path: String = "/", duration: Long = 60, timeUnit: TimeUnit = TimeUnit.SECONDS) : String
    {

        clearGraph()
        importAsGraphMl(name, path)
        processLayout(duration, timeUnit)
        return exportAsJsonString()
    }


    private fun clearGraph()
    {
        graphModel.graph.clear()
    }

    private fun processLayout(duration : Long = 60, timeUnit : TimeUnit = TimeUnit.SECONDS)
    {
        val autoLayout = AutoLayout(duration, timeUnit)
        autoLayout.setGraphModel(graphModel)
        val firstLayout = YifanHuLayout(null, StepDisplacement(1f))
        val secondLayout = ForceAtlasLayout(null)
        val adjustBySizeProperty = AutoLayout.createDynamicProperty(
            "forceAtlas.adjustSizes.name",
            java.lang.Boolean.TRUE,
            0.1f
        )//True after 10% of layout time
        val repulsionProperty = AutoLayout.createDynamicProperty(
            "forceAtlas.repulsionStrength.name",
            500.0,
            0f
        )//500 for the complete period
        autoLayout.addLayout(firstLayout, 0.5f)
        autoLayout.addLayout(secondLayout, 0.5f, arrayOf(adjustBySizeProperty, repulsionProperty))
        autoLayout.execute()
    }

    private fun importAsGraphMl(name : String = "neo4j_tmp_query_result.graphml", path : String = "/")
    {
        val importController = Lookup.getDefault().lookup(ImportController::class.java);

        //Import file
        val container : Container
        try {
            val file = File(javaClass.getResource(path + name).toURI())
            container = importController.importFile(file);
            container.getLoader().setEdgeDefault(EdgeDirectionDefault.DIRECTED);   //Force DIRECTED
            container.getLoader().setAllowAutoNode(false);  //Don't create missing nodes
            println(file.readText())
        } catch (ex : Exception) {
            ex.printStackTrace();
            println("there is no file for me :(")
            return
        }

        //Append imported data to GraphAPI
        importController.process(container, DefaultProcessor(), workspace);
    }

    private fun exportAsJsonFile(name: String = "wtf.json", path : String = "./")
    {
        val ec = Lookup.getDefault().lookup(ExportController::class.java)
        val exporter = JSONExporterBuilder().buildExporter()
        val characterExporter = exporter as CharacterExporter
        val fileWriter = File(path + name).writer()
        ec.exportWriter(fileWriter, characterExporter)
    }

    private fun exportAsJsonString() : String
    {
        val ec = Lookup.getDefault().lookup(ExportController::class.java)
        val exporter = JSONExporterBuilder().buildExporter()
        val characterExporter = exporter as CharacterExporter
        val stringWriter = StringWriter()
        ec.exportWriter(stringWriter, characterExporter)
        return stringWriter.toString()
    }


}
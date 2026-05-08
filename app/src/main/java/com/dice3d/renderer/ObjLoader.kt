package com.dice3d.renderer

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.sqrt

data class ModelData(
    val vertices: FloatArray,
    val normals: FloatArray,
    val texCoords: FloatArray,
    val vertexCount: Int
)

object ObjLoader {
    private const val TAG = "ObjLoader"

    fun load(context: Context, fileName: String, scale: Float = 0.5f): ModelData? {
        return try {
            val reader = BufferedReader(InputStreamReader(context.assets.open(fileName)))
            val content = reader.readText()
            reader.close()
            parseFromString(content, scale)
        } catch (e: Exception) { Log.e(TAG,"Failed to load OBJ: $fileName",e); null }
    }

    fun parseFromString(content: String, scale: Float = 0.5f): ModelData? {
        val alv = mutableListOf<Float>()
        val altexCoord = mutableListOf<Float>()
        val alvResult = mutableListOf<Float>()
        val alFaceIndex = mutableListOf<Int>()
        val altexResultCoord = mutableListOf<Float>()
        val normalMap = hashMapOf<Int, MutableList<FloatArray>>()

        try {
            content.lineSequence().forEach { line ->
                val parts = line.trim().split(Regex("\\s+"))
                if (parts.isEmpty() || parts[0].isEmpty()) return@forEach
                when (parts[0]) {
                    "v" -> { alv.add(parts[1].toFloat()*scale); alv.add(parts[2].toFloat()*scale); alv.add(parts[3].toFloat()*scale) }
                    "vt" -> { altexCoord.add(parts[1].toFloat()); altexCoord.add(-parts[2].toFloat()) }
                    "f" -> {
                        val faceVertexIndices = mutableListOf<Int>()
                        val faceTexIndices = mutableListOf<Int>()
                        for (i in 1 until parts.size) {
                            val fp = parts[i].split("/")
                            faceVertexIndices.add(fp[0].toInt()-1)
                            if (fp.size>1 && fp[1].isNotEmpty()) faceTexIndices.add(fp[1].toInt()-1)
                            else faceTexIndices.add(-1)
                        }
                        val triangles = triangulateFace(faceVertexIndices.size)
                        for (tri in triangles) {
                            val indices = intArrayOf(faceVertexIndices[tri[0]], faceVertexIndices[tri[1]], faceVertexIndices[tri[2]])
                            val texIndices = intArrayOf(faceTexIndices[tri[0]], faceTexIndices[tri[1]], faceTexIndices[tri[2]])
                            for (j in 0..2) {
                                val vi = indices[j]
                                alvResult.add(alv[3*vi]); alvResult.add(alv[3*vi+1]); alvResult.add(alv[3*vi+2])
                                if (texIndices[j] >= 0 && texIndices[j] < altexCoord.size/2) { altexResultCoord.add(altexCoord[2*texIndices[j]]); altexResultCoord.add(altexCoord[2*texIndices[j]+1]) }
                                else { altexResultCoord.add(0f); altexResultCoord.add(0f) }
                            }
                            alFaceIndex.add(indices[0]); alFaceIndex.add(indices[1]); alFaceIndex.add(indices[2])
                            val v0=floatArrayOf(alv[3*indices[0]],alv[3*indices[0]+1],alv[3*indices[0]+2])
                            val v1=floatArrayOf(alv[3*indices[1]],alv[3*indices[1]+1],alv[3*indices[1]+2])
                            val v2=floatArrayOf(alv[3*indices[2]],alv[3*indices[2]+1],alv[3*indices[2]+2])
                            val e1=floatArrayOf(v1[0]-v0[0],v1[1]-v0[1],v1[2]-v0[2])
                            val e2=floatArrayOf(v2[0]-v0[0],v2[1]-v0[1],v2[2]-v0[2])
                            val normal=crossProduct(e1,e2); normalize(normal)
                            for (idx in indices) normalMap.getOrPut(idx){mutableListOf()}.add(normal.copyOf())
                        }
                    }
                }
            }
            val vertices = alvResult.toFloatArray(); val texCoords = altexResultCoord.toFloatArray()
            val normals = FloatArray(alFaceIndex.size*3); var c=0
            for (idx in alFaceIndex) { val avg=averageNormals(normalMap[idx]?:return null); normals[c++]=avg[0]; normals[c++]=avg[1]; normals[c++]=avg[2] }
            return ModelData(vertices, normals, texCoords, vertices.size/3)
        } catch (e: Exception) { Log.e(TAG,"Failed to parse OBJ",e); return null }
    }

    private fun triangulateFace(vertexCount: Int): List<IntArray> {
        val result = mutableListOf<IntArray>()
        for (i in 1 until vertexCount - 1) {
            result.add(intArrayOf(0, i, i + 1))
        }
        return result
    }

    private fun crossProduct(a: FloatArray, b: FloatArray) = floatArrayOf(a[1]*b[2]-b[1]*a[2], a[2]*b[0]-b[2]*a[0], a[0]*b[1]-b[0]*a[1])
    private fun normalize(v: FloatArray) { val len=sqrt((v[0]*v[0]+v[1]*v[1]+v[2]*v[2]).toDouble()).toFloat(); if(len>0.0001f){v[0]/=len;v[1]/=len;v[2]/=len} }
    private fun averageNormals(normals: List<FloatArray>): FloatArray { val r=floatArrayOf(0f,0f,0f); for(n in normals){r[0]+=n[0];r[1]+=n[1];r[2]+=n[2]}; normalize(r); return r }
}

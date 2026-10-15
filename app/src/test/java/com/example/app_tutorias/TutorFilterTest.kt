package com.example.app_tutorias

import com.example.app_tutorias.data.MockData
import com.example.app_tutorias.data.filterTutors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TutorFilterTest {
    @Test
    fun `empty filters return every tutor`() {
        val result = filterTutors(MockData.tutors, "", null)

        assertEquals(MockData.tutors, result)
    }

    @Test
    fun `query filters by tutor name or specialty ignoring case`() {
        val byName = filterTutors(MockData.tutors, "ana", null)
        val bySpecialty = filterTutors(MockData.tutors, "PROGRAMACIÓN", null)

        assertEquals(listOf("Ana Morales"), byName.map { it.name })
        assertEquals(listOf("Diego López"), bySpecialty.map { it.name })
    }

    @Test
    fun `category and query filters are combined`() {
        val result = filterTutors(MockData.tutors, "física", "science")
        val noResults = filterTutors(MockData.tutors, "inglés", "math")

        assertEquals(listOf("Carlos Méndez"), result.map { it.name })
        assertTrue(noResults.isEmpty())
    }
}

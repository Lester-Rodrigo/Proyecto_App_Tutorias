package com.example.app_tutorias.data

fun filterTutors(
    tutors: List<Tutor>,
    query: String,
    selectedCategoryId: String?
): List<Tutor> {
    val normalizedQuery = query.trim()

    return tutors.filter { tutor ->
        val matchesQuery = normalizedQuery.isEmpty() ||
            tutor.name.contains(normalizedQuery, ignoreCase = true) ||
            tutor.specialty.contains(normalizedQuery, ignoreCase = true)
        val matchesCategory = selectedCategoryId == null ||
            selectedCategoryId in tutor.categoryIds

        matchesQuery && matchesCategory
    }
}

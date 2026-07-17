package com.sepring.template.repository

import com.sepring.template.model.FileRecord
import org.springframework.data.jpa.repository.JpaRepository

interface FileRecordRepository : JpaRepository<FileRecord, Long>

package com.achals.raft.guice

import com.achals.raft.dao.PersistentStateDao
import com.achals.raft.dao.memory.InMemoryStateDao

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule

/**
 * Created by achalshah on 10/3/14.
 */
class DaoModule extends AbstractModule with ScalaModule {
    override def configure (): Unit = {
        bind[PersistentStateDao].to[InMemoryStateDao]
    }
}

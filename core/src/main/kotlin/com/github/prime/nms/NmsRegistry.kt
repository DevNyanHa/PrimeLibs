package com.github.prime.nms

import com.github.prime.nms.version.NmsVersion
import java.util.ServiceLoader

/**
 * 등록된 [NmsAdapter] 구현체를 탐색하고 서버 버전에 맞는 어댑터를 선택합니다.
 *
 * @property classLoader ServiceLoader가 어댑터 구현체를 탐색할 클래스 로더
 */
class NmsRegistry(
    private val classLoader: ClassLoader = NmsRegistry::class.java.classLoader
) {
    private val nmsAdapters: List<NmsAdapter> by lazy {
        ServiceLoader
            .load(NmsAdapter::class.java, classLoader)
            .toList()
            .distinctBy(NmsAdapter::id)
            .sortedByDescending { it.nmsVersionRange.minimum }
    }

    /**
     * [NmsVersion]을 지원하는 첫 번째 [NmsAdapter]를 반환합니다.
     *
     * @param version 확인할 [NmsVersion]
     * @return 버전을 지원하는 [NmsAdapter]. 없으면 null입니다.
     */
    fun resolve(version: NmsVersion): NmsAdapter? = nmsAdapters.firstOrNull { it.nmsVersionRange.contains(version) }
}
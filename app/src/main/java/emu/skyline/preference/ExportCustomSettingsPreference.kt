/*
 * SPDX-License-Identifier: MPL-2.0
 * Copyright © 2023 Strato Team and Contributors (https://github.com/strato-emu/)
 */

package emu.skyline.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.content.ClipData
import android.content.ClipboardManager
import emu.skyline.settings.EmulationSettings

/**
 * Shows a dialog to copy the current game's custom settings (or the global settings if customs are disabled) so they
 * can be easily shareable in Discord or other places
 */
class ExportCustomSettingsPreference @JvmOverloads constructor(context : Context, attrs : AttributeSet? = null, defStyleAttr : Int = androidx.preference.R.attr.preferenceStyle) : Preference(context, attrs, defStyleAttr) {

    init {
        setOnPreferenceClickListener {
            var emulationSettings = EmulationSettings.forPrefName(preferenceManager.sharedPreferencesName)

            if (!emulationSettings.useCustomSettings) {
                emulationSettings = EmulationSettings.global
            }

            val systemIsDocked = emulationSettings.isDocked;

            val gpuDriver = emulationSettings.gpuDriver;
            val gpuTripleBuffering = emulationSettings.forceTripleBuffering;
            val gpuExecSlotCount = emulationSettings.executorSlotCountScale
            val gpuExecFlushThreshold = emulationSettings.executorFlushThreshold;
            val gpuDMI = emulationSettings.useDirectMemoryImport;
            val gpuFreeGuestTextureMemory = emulationSettings.freeGuestTextureMemory;
            val gpuDisableShaderCache = emulationSettings.disableShaderCache;
            val gpuForceMaxGpuClocks = emulationSettings.forceMaxGpuClocks

            val hackFastGpuReadback = emulationSettings.enableFastGpuReadbackHack;
            val hackFastReadbackWrite = emulationSettings.enableFastReadbackWrites;
            val hackDisableSubgroupShuffle = emulationSettings.disableSubgroupShuffle;

            var hh="$gpuDriver";

            if(hh=="system"){
                hh="系统驱动";
            }

            val settingsAsText = String.format(
                """
                系统
                - 主机模式: $systemIsDocked
                
                GPU
                - 驱动: $hh
                - 执行器 : $gpuExecSlotCount 插槽 (刷新阈值: $gpuExecFlushThreshold)
                - 三重缓冲: $gpuTripleBuffering, 直接内存: $gpuDMI
                - 最大GPU时钟: $gpuForceMaxGpuClocks, 清理空闲纹理内存: $gpuFreeGuestTextureMemory
                - 禁用着色器缓存: $gpuDisableShaderCache
                
                黑客
                - 快速GPU回读: $hackFastGpuReadback, 快速回读写入 $hackFastReadbackWrite
                - 禁用GPU子组随机播放: $hackDisableSubgroupShuffle
                """.trimIndent().replace("true", "✔").replace("false", "✖")
            )

            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(settingsAsText)
                .setPositiveButton(android.R.string.copy) { _, _ ->
                    // Copy the current settings as text to the system clipboard
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("label", settingsAsText)
                    clipboard.setPrimaryClip(clip)

                }
                .setNegativeButton(android.R.string.ok, null)
                .show()

            true
        }
    }
}

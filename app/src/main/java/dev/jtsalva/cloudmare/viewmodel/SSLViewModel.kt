package dev.jtsalva.cloudmare.viewmodel

import android.view.View
import android.widget.AdapterView
import androidx.databinding.BaseObservable
import dev.jtsalva.cloudmare.R
import dev.jtsalva.cloudmare.SSLActivity
import dev.jtsalva.cloudmare.api.zone.Zone
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSetting
import dev.jtsalva.cloudmare.api.zonesettings.ZoneSettingRequest
import timber.log.Timber

class SSLViewModel(
    private val activity: SSLActivity,
    private val domain: Zone
) : BaseObservable(), AdapterView.OnItemSelectedListener {

    var isFinishedInitializing = false

    val sslModeTranslator = ZoneSetting.SSLModeTranslator(activity)

    var sslMode = ZoneSetting.SSL_MODE_OFF
        set(value) {
            if (value != field && isFinishedInitializing)
                ZoneSettingRequest(activity).launch {
                    val response = update(domain.id, ZoneSetting("ssl", value))

                    if (response.success) field = value
                    else activity.dialog.
                        error(title = "Can't set ssl mode", message = response.firstErrorMessage)
                }
            else field = value
        }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        val selectedItem = parent.getItemAtPosition(pos)

        when (parent.id) {
            R.id.ssl_mode_spinner -> with (activity) {
                sslMode = sslModeTranslator.getId(selectedItem.toString())
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        Timber.d("Nothing selected")
    }

}
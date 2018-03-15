package jp.gr.java_conf.cody.ui

import com.stephentuso.welcome.WelcomeActivity
import com.stephentuso.welcome.WelcomeConfiguration
import com.stephentuso.welcome.BasicPage
import jp.gr.java_conf.cody.R


/**
 * Created by daigo on 2018/03/14.
 */
class IntroActivity : WelcomeActivity() {
    override fun configuration(): WelcomeConfiguration {
        return WelcomeConfiguration.Builder(this)
                .defaultBackgroundColor(R.color.intro_blue)
                .page(BasicPage(R.drawable.intro1,
                        getString(R.string.intro1_title),
                        getString(R.string.intro1_content))
                        .background(R.color.intro1)
                )
                .page(BasicPage(R.drawable.intro2,
                        getString(R.string.intro2_title),
                        getString(R.string.intro2_content))
                        .background(R.color.intro2)
                )
                .page(BasicPage(R.drawable.intro3,
                        getString(R.string.intro3_title),
                        getString(R.string.intro3_content))
                        .background(R.color.intro3)
                )
                .page(BasicPage(R.drawable.intro4,
                        getString(R.string.intro4_title),
                        getString(R.string.intro4_content))
                        .background(R.color.intro4)
                )
                .swipeToDismiss(true)
                .build()
    }
}
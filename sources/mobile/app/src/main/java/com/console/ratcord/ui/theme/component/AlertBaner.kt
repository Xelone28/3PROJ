import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AlertBaner(
    message: String,
    durationMillis: Long = 2000,
    onAnimationEnd: () -> Unit

) {
    var isVisible by remember { mutableStateOf(true) }
    val alpha = remember { Animatable(1f) }
    val offsetY = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        delay(durationMillis)

        alpha.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 1000, easing = EaseOutSine)
        )
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 0, easing = EaseOutSine) // text animation
        )
        isVisible = false
        onAnimationEnd()
    }

    if (isVisible) {
        Surface(
            color = Color.Gray.copy(alpha = alpha.value),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .offset(y = offsetY.value.dp)
                    .wrapContentSize()
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(8.dp),
                    color = Color.White
                )
            }
        }
    }
}

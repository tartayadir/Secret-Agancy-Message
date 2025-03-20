$NATS_CLI_PATH = "C:\D\app\nats-0.1.6-windows-amd64"
cd $NATS_CLI_PATH
# nats-test.ps1

# Очистка предыдущего файла вывода
$outputFile = "nats_output.txt"
if (Test-Path $outputFile) {
    Remove-Item $outputFile
}

# Скрипт для сессии 1 (Executor)
$executor = {
    param ($outputFile)

    # Шаг 1: Выполнить первую команду и сохранить вывод
    $message = "Test1"
    $output1 = & nats pub save.msg $message --reply receive.msg
    $output1 | Out-File -FilePath $outputFile

    # Ждать, пока сессия 2 прочитает вывод (даем время)
    Start-Sleep -Seconds 2

    # Шаг 2: Использовать первый вывод как сообщение для второй команды
    $firstOutput = Get-Content -Path $outputFile
    $output2 = & nats pub receive.msg $firstOutput --reply receive.msg
    $output2 | Out-File -FilePath $outputFile
}

# Скрипт для сессии 2 (Reader)
$reader = {
    param ($outputFile)

    # Ждать, пока файл появится
    while (-not (Test-Path $outputFile)) {
        Start-Sleep -Seconds 1
    }

    # Читать первый вывод
    $firstOutput = Get-Content -Path $outputFile
    Write-Host "Session 2 read first output: $firstOutput"

    # Ждать обновления файла для второго вывода
    Start-Sleep -Seconds 3  # Дать время сессии 1 записать второй вывод
    $secondOutput = Get-Content -Path $outputFile
    Write-Host "Session 2 read final output: $secondOutput"
}

# Запустить сессию 1 в фоновом режиме
$job = Start-Job -ScriptBlock $executor -ArgumentList $outputFile -Name "Executor"

# Запустить сессию 2 в основном потоке
Write-Host "Starting NATS test..."
Invoke-Command -ScriptBlock $reader -ArgumentList $outputFile

# Ожидание завершения фоновой задачи и очистка (опционально)
Wait-Job -Job $job
Remove-Job -Job $job
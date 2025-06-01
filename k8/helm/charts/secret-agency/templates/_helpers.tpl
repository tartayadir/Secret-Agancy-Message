{{- define "gen.password" -}}
{{- $input := printf "%s-%s" .Release.Name .passwordKey -}}
{{- $hashed := sha256sum $input | trunc 16 -}}
{{- $hashed | quote -}}
{{- end -}}

{{/* Пароль root пользователя базы данных */}}
{{- define "db.root-password" -}}
{{- include "gen.password" (dict "Release" .Release "passwordKey" "db.root") -}}
{{- end -}}

{{/* Пароль обычного пользователя базы данных */}}
{{- define "db.user-password" -}}
{{- include "gen.password" (dict "Release" .Release "passwordKey" "db.user") -}}
{{- end -}}

{{/* Пароль пользователя NATS */}}
{{- define "nats.user-password" -}}
{{- include "gen.password" (dict "Release" .Release "passwordKey" "nats.user") -}}
{{- end -}}

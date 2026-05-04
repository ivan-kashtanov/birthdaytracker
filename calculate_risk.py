import json
import os

REPORT_DIR = "security-reports"

TRIVY_IMAGE_REPORT = os.path.join(REPORT_DIR, "trivy-image-report.json")
TRIVY_CONFIG_REPORT = os.path.join(REPORT_DIR, "trivy-config-report.json")
SNYK_REPORT = os.path.join(REPORT_DIR, "snyk-report.json")
GRYPE_REPORT = os.path.join(REPORT_DIR, "grype-report.json")

WEIGHTS = {
    "CRITICAL": 10,
    "HIGH": 7,
    "MEDIUM": 4,
    "LOW": 1,
    "UNKNOWN": 1
}


def add_score(severity, counters):
    severity = str(severity).upper()
    if severity not in counters:
        counters[severity] = 0
    counters[severity] += 1


def parse_trivy(report_path, counters):
    if not os.path.exists(report_path):
        return

    with open(report_path, "r", encoding="utf-8") as file:
        data = json.load(file)

    for result in data.get("Results", []):
        for vulnerability in result.get("Vulnerabilities", []):
            add_score(vulnerability.get("Severity", "UNKNOWN"), counters)

        for misconfiguration in result.get("Misconfigurations", []):
            add_score(misconfiguration.get("Severity", "UNKNOWN"), counters)


def parse_snyk(report_path, counters):
    if not os.path.exists(report_path):
        return

    with open(report_path, "r", encoding="utf-8") as file:
        data = json.load(file)

    for vulnerability in data.get("vulnerabilities", []):
        add_score(vulnerability.get("severity", "UNKNOWN"), counters)


def parse_grype(report_path, counters):
    if not os.path.exists(report_path):
        return

    with open(report_path, "r", encoding="utf-8") as file:
        data = json.load(file)

    for match in data.get("matches", []):
        vulnerability = match.get("vulnerability", {})
        add_score(vulnerability.get("severity", "UNKNOWN"), counters)


def calculate_risk(counters):
    risk = 0
    for severity, count in counters.items():
        risk += WEIGHTS.get(severity, 1) * count
    return risk


def get_decision(risk):
    if risk >= 50:
        return "DENY"
    if risk >= 20:
        return "REVIEW"
    return "ALLOW"


def main():
    counters = {
        "CRITICAL": 0,
        "HIGH": 0,
        "MEDIUM": 0,
        "LOW": 0,
        "UNKNOWN": 0
    }

    parse_trivy(TRIVY_IMAGE_REPORT, counters)
    parse_trivy(TRIVY_CONFIG_REPORT, counters)
    parse_snyk(SNYK_REPORT, counters)
    parse_grype(GRYPE_REPORT, counters)

    risk = calculate_risk(counters)
    decision = get_decision(risk)

    result = {
        "risk": risk,
        "decision": decision,
        "summary": counters
    }

    output_path = os.path.join(REPORT_DIR, "risk-decision.json")
    with open(output_path, "w", encoding="utf-8") as file:
        json.dump(result, file, indent=4, ensure_ascii=False)

    print(risk)


if __name__ == "__main__":
    main()